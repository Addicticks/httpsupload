/*
 * Copyright Addicticks 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addicticks.net.httpsupload;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;

/**
 * Uploading file(s) to a HTTP/HTTPS site using POST method and the
 * {@code multipart/form-data} encoding (RFC2388).
 * .<p>
 * <br>
 * The class has the following features:<br>
 * <ul>
 * <li>Allows uploading multiple files (or streams) in one operation (if supported by the endpoint).</li>
 * </ul><br>
 * <ul>
 * <li>Has no external dependencies, e.g. no Apache HttpClient.</li>
 * </ul><br>
 * <ul>
 * <li>Certificate validation can be turned off. This is important when working
 * against a site that uses a self-signed certificate.</li>
 * </ul><br>
 * <ul>
 * <li>Support for endpoints that require authentication (only authentication type "Basic"
 * is currently supported).</li>
 * </ul><br>
 * <ul>
 * <li>Java's <code>HttpURLConnection</code>/<code>HttpsURLConnection</code>
 * will normally do internal buffering of the request being sent. If the file
 * being uploaded is large this means that Java will try to buffer all of the
 * file in memory. This class avoids this by streaming the file contents to the
 * server and therefore allows very large files to be uploaded without causing an
 * out-of-memory error in the JVM. If you are uploading from a stream rather than
 * a file then this feature does not apply.</li>
 * </ul><br>
 * <ul>
 * <li>Support for explicitly setting network proxy. This is implemented <i>without</i>
 * the use of global system properties, so can be used without affecting other
 * classes in the same JVM.</li>
 * </ul><br>
 * <ul>
 * <li>Upload progress can be tracked for every 1% progress. This allows a
 * UI to let the user know how the upload is progressing.</li>
 * </ul><br>
 * <br>
 * <br>
 * EXAMPLE:<br>
 * <br>
 * <pre class="brush:java">
 * // Configure the connection        
 * HttpsFileUploaderConfig uploaderConfig = new HttpsFileUploaderConfig(endpointURL);
 * uploaderConfig.setEndpointUsername(endpointUsername);
 * uploaderConfig.setEndpointPassword(endpointPassword);
 * uploaderConfig.setValidateCertificates(false);
 * uploaderConfig.setProxyAddress(proxyAddress);
 * uploaderConfig.setProxyPort(proxyPort);
 * 
 * // Do the upload.
 * // A single file is uploaded along with a single text field.
 * result = HttpsFileUploader.upload(
 *     uploaderConfig, 
 *     Collections.singletonList(UploadItemFile(new File("hugefile.zip"))), 
 *     Collections.singletonMap("email", "johnny@company.com"), 
 *     this);
 * 
 * // Evaluate the result.
 * if (!result.isError()) {
 *     System.out.println("OK, upload successful");
 * } else {
 *     System.out.println("Error uploading, http code :" + result.getHttpStatusCode());
 *     System.out.println("Message from server : " + result.getResponseTextNoHtml());
 * }
 * 
 * </pre>
 * 
 * @author Addicticks 
 */
public class HttpsFileUploader  {


    
    private static final int CHUNK_SIZE_IN_BYTES = 8192;
    
    // Sending POST multi-part data requires literals
    private static final String CRLF = "\r\n";
    private static final String TWOHYPHENS = "--";
    private static final String MULTIPART_BOUNDARY = "*****X99611299X******";  // random boundary
    
    
   private HttpsFileUploader() {
   }
          
    

 
    
    

    /**
     * Uploads a file (or files) using POST method. The file is never cached in memory so 
     * very big files can be uploaded without causing a memory problem. Optionally allows
     * upload directly from an {@code InputStream} rather than a physical file.
     * 
     * <p>After the method returns the result should be examined for errors.
     * 
     * <p>Multiple files can be uploaded in the same method call and there's no
     * requirement that each file is uploaded into its own field name. Whether or 
     * not form field names for multiple files should be unique or not depends on 
     * the capabilities of the endpoint.
     * 
     * <p>Some endpoints also allow uploading of other information than just the file(s). This is 
     * supported via the <code>otherFields</code> argument.<br>
     * 
     * <p>This is a blocking call. You may want to spawn it off into its own thread.<br>
     * <br>
     * <br>
     * <br>
     * See general description and example {@link HttpsFileUploader here}.<br>
     * <br>
     * 
     * @param config configuration for the connection.
     * @param uploadFiles the files or streams to upload. The files will be uploaded
     * in the order of this list.
     * @param otherFields Other fields to be POST'ed into the form besides the file(s). Some forms allow
     * certain other fields, such as the name or email address of the uploader. The key of the map must
     * be the form field name and the value is the plain text value of the field. The {@code null} value 
     * is acceptable.
     * @param progressNotifier Optional callback notifier. This will give a callback for each percent of the file
     * that has been uploaded. Use <code>null</code> if no progress notification is required.
     * @return result of the upload operation
     * @throws IOException if the endpoint cannot be reached or if input file(s) cannot be
     * read.
     */
    public static HttpsFileUploaderResult upload(
            HttpsFileUploaderConfig config,
            List<? extends UploadItem> uploadFiles, 
            Map<String,String> otherFields,
            UploadProgress progressNotifier) throws IOException {
        
        boolean byteSizeIsKnown = byteSizeIsKnown(uploadFiles);
        
        // Setup the connection
        HttpURLConnection httpsUrlConnection = setup(config);
        
        long totalBytes = 0;
        ArrayList<UploadFile> uploadFilesX = new ArrayList();
        ArrayList<UploadOtherField> uploadFieldsX = new ArrayList();
        String globalFooter =  TWOHYPHENS + MULTIPART_BOUNDARY + TWOHYPHENS + CRLF;


        // Arrange the files to be uploaded into a new data structure
        // which includes MIME multi-part headers and footers. The only reason
        // for doing it this way is to be able to calculate the size of all
        // components BEFORE actually sending any data.
        for (UploadItem uItem : uploadFiles) {
            String uploadFormFieldName =  uItem.getFormFieldName();
            String mimeType = uItem.getMimeType();
            String hintFilename = uItem.getHintFilename();
            ArrayList<String> mpHeaders = new ArrayList();
            ArrayList<String> mpFooters = new ArrayList();

            // Multi-part headers
            mpHeaders.add(TWOHYPHENS + MULTIPART_BOUNDARY + CRLF);
            mpHeaders.add("Content-Disposition: form-data; name=\"" + uploadFormFieldName + "\";filename=\"" + hintFilename + "\"" + CRLF);
            mpHeaders.add("Content-Type: " + mimeType + CRLF);
            mpHeaders.add(CRLF);

            // Multi-part footers
            mpFooters.add(CRLF);
            
            UploadFile uploadFile = new UploadFile(
                    uploadFormFieldName,
                    uItem,
                    mpHeaders,
                    mpFooters
                    );
            uploadFilesX.add(uploadFile);
            totalBytes = totalBytes + uploadFile.getTotalByteSize();
        }
        
        // Arrange the fields to be uploaded into a new data structure
        // which includes MIME multi-part headers and footers. The only reason
        // for doing it this way is to be able to calculate the size of all
        // components BEFORE actually sending any data.
        if (otherFields != null) {
            for (String otherField : otherFields.keySet()) {
                ArrayList<String> mpHeaders = new ArrayList();
                ArrayList<String> mpFooters = new ArrayList();

                // Multi-part headers
                mpHeaders.add(TWOHYPHENS + MULTIPART_BOUNDARY + CRLF);
                mpHeaders.add("Content-Disposition: form-data; name=\"" + otherField + "\"" + CRLF);
                //mpHeaders.add("Content-Type: text/plain" + CRLF);
                mpHeaders.add(CRLF);

                // Multi-part footers
                mpFooters.add(CRLF);

                UploadOtherField uploadOtherField = new UploadOtherField(
                        otherField,
                        otherFields.get(otherField),
                        mpHeaders,
                        mpFooters);

                uploadFieldsX.add(uploadOtherField);
                totalBytes = totalBytes + uploadOtherField.getTotalByteSize();
            }
        }

        totalBytes = totalBytes + globalFooter.length();
        
        // Set the total length. This is the length of the
        // multi-part header + the length of the field content itself +
        // the length of the multi-part footer. (for all fields)
        // If we did NOT use this method then Java would cache the entire contents of the POST 
        // data in memory because it has to try to figure out what to set the
        // Content-Length HTTP header field to before actually sending the data.
        // For large files such behaviour would most likely cause memory
        // problems.
        // We can only does this is we know the upload size. If we are uploading
        // from a stream then we cannot do this because the size is not known in advance.
        if (byteSizeIsKnown) {
            httpsUrlConnection.setFixedLengthStreamingMode(totalBytes);
        }
        
        
        long startTime = System.currentTimeMillis();
        
        // Open the data stream. This is where the connection is physically 
        // established.
        try (DataOutputStream out = new DataOutputStream(httpsUrlConnection.getOutputStream())) {
            
            // Calculate the total number of file bytes that will be sent
            // (accross all files)
            long totalDataBytes=0;
            for (UploadFile uploadFile : uploadFilesX) {
                totalDataBytes += (uploadFile.getUploadItem().getSizeInBytes() == -1) ? 0 : uploadFile.getUploadItem().getSizeInBytes();
            }
            if (progressNotifier != null) {
                progressNotifier.uploadStart(uploadFilesX.size(), totalDataBytes);
            }
            
            long totalDataBytesSent = 0;
            //  *********************
            // SENDING  FILES
            //  *********************
            for (UploadFile uploadFile : uploadFilesX) {
                
                
                // Write multi-part headers to output stream
                for (String str : uploadFile.getMpHeaders()) {
                    out.writeBytes(str);
                }
                
                // Write file
                try (InputStream is = uploadFile.getUploadItem().getInputStream()) {
                    byte[] buffer = new byte[CHUNK_SIZE_IN_BYTES];
                    int bytes_read;
                    int total_written=0;
                    int filesize = (int) uploadFile.getUploadItem().getSizeInBytes();
                    
                    // In order to calculate pct completed we use a hardcoded size of 512 KBytes
                    // for streams (since we do not know the actual size) and the actual size if
                    // it's a file.
                    int notif_size_total = (int)((uploadFile.getUploadItem().getSizeInBytes() == -1) ? 512*1024 : uploadFile.getUploadItem().getSizeInBytes());
                    int notif_size = notif_size_total/100; // how often, in bytes, do we notify ?
                    long last_notif = 0;
                    int prevPct = -1;
                    
                    // Make sure we start at 0%
                    prevPct = notifyProgress(
                            progressNotifier,
                            uploadFile.getUploadItem().getFile(),
                            0,
                            filesize,
                            prevPct);
                    while ((bytes_read = is.read(buffer)) != -1) {
                        out.write(buffer, 0, bytes_read);
                        total_written = total_written + bytes_read;
                        if ((total_written - last_notif) >= notif_size) {
                            // notify about progress
                            prevPct = notifyProgress(
                                    progressNotifier,
                                    uploadFile.getUploadItem().getFile(),
                                    total_written,
                                    filesize,
                                    prevPct);
                            last_notif = total_written;
                        }
                    }
                    // Make sure we end at 100%
                    notifyProgress(
                            progressNotifier,
                            uploadFile.getUploadItem().getFile(),
                            filesize,
                            filesize,
                            prevPct);
                    
                    totalDataBytesSent += total_written;
                }
                
                // Write multi-part footers to output stream
                for (String str : uploadFile.getMpFooters()) {
                    out.writeBytes(str);
                }
                out.flush();
            }
            
            
            //  *********************
            // SENDING OTHER FIELDS
            //  *********************

            for (UploadOtherField uploadOtherField : uploadFieldsX) {

                // Write multi-part headers to output stream
                for (String str : uploadOtherField.getMpHeaders()) {
                    out.writeBytes(str);
                }

                out.writeBytes(uploadOtherField.getFieldValue());

                // Write multi-part footers to output stream
                for (String str : uploadOtherField.getMpFooters()) {
                    out.writeBytes(str);
                }
            }
            
            //  *********************
            // SENDING GLOBAL FOOTER
            //  *********************
            out.writeBytes(globalFooter);

            // End notification
            if (progressNotifier != null) {
                progressNotifier.uploadEnd(totalDataBytesSent, System.currentTimeMillis() - startTime);
            }
        }
        
        
        
        
        
        int httpStatus = httpsUrlConnection.getResponseCode();
        
        StringBuilder stringBuilder = null;
        if (httpStatus != HttpsURLConnection.HTTP_UNAUTHORIZED) {
            try (BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpsUrlConnection.getInputStream())))) {
                String line;
                stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            } catch (IOException ex) {
                // Read the Err stream
                try (BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpsUrlConnection.getErrorStream())))) {
                    String line;
                    stringBuilder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append(System.lineSeparator());
                    }
                }
            }
        }

        String responseText = null;
        if (stringBuilder != null) {
            responseText = stringBuilder.toString();
        }
        

        // We could potentially call disconnect() on the httpsUrlConnection
        // but Java will pool HTTP connections behind our back anyway and keep
        // them alive for typically 5 seconds. Thus disconnect() should only
        // be called if we do not want to connect to the same server again during
        // the next x seconds. Since we do not know this it is more efficient to 
        // let Java handle the connection closedown.
        
        return new HttpsFileUploaderResult(httpStatus, responseText);
    }
    
    
    
    /**
     * Uploads a file. This is a convenience method of the more general
     * {@link #upload(com.addicticks.net.httpsupload.HttpsFileUploaderConfig, java.util.Map, java.util.Map, com.addicticks.net.httpsupload.UploadProgress) upload(...)} method.
     * This method only uploads a single file and expects the destination field for
     * the file on the server to be named {@code "file"}.
     *
     * <p>After the method returns the result should be examined for errors.
     * 
     * @see #upload(com.addicticks.net.httpsupload.HttpsFileUploaderConfig, java.util.Map, java.util.Map, com.addicticks.net.httpsupload.UploadProgress) 
     * @param config configuration for the connection.
     * @param uploadFile file to upload
     * @return result of the upload operation

     * @throws IOException if the endpoint cannot be reached or if input file cannot be
     * read.
     */
    public static HttpsFileUploaderResult upload(
            HttpsFileUploaderConfig config,
            File uploadFile) throws IOException {
        
        return upload(
                config, 
                Collections.singletonList(new UploadItemFile(uploadFile)), 
                null, 
                null);
    }
    
    
    /**
     * Determines if there are upload items where the size is 
     * unknown in advance. This means we cannot predict the total upload size.
     * @param uploadItems
     * @return 
     */
    private static boolean byteSizeIsKnown(Collection<? extends UploadItem> uploadItems) {
        for (UploadItem uploadItem : uploadItems) {
            if (uploadItem.getSizeInBytes() == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Configures the HTTP/HTTPS connection.
     * @param config
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    private static HttpURLConnection setup(HttpsFileUploaderConfig config) throws MalformedURLException, IOException {
        HttpURLConnection httpUrlConnection;
        boolean isHttps = false;
        URL url = config.getURL();
        if (url.getProtocol().equalsIgnoreCase("https")) {
            isHttps = true;
        }

        if (config.usesProxy()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyAddress(), config.getProxyPort()));
            httpUrlConnection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
        }
        
        
        if (!config.isValidateCertificates() && isHttps) {
            SSLUtils.setNoValidate(((HttpsURLConnection) httpUrlConnection), config.getAcceptedIssuers());
        }
        
        
        httpUrlConnection.setUseCaches(false);  // do not use connection caches
        httpUrlConnection.setDoOutput(true);

        // Set timeouts
        httpUrlConnection.setConnectTimeout(config.getConnectTimeoutMs());
        httpUrlConnection.setReadTimeout(config.getReadTimeoutMs());

        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
        httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + MULTIPART_BOUNDARY);
        
        // Sets additional headers
        if (config.getAdditionalHeaders() != null) {
            for(Entry<String,String> e : config.getAdditionalHeaders().entrySet()) {
                // We use the 'set' method here and not the 'add' method because
                // it should be allowed for example for the user to override Java's default
                // User-Agent value. 
                httpUrlConnection.setRequestProperty(e.getKey(), e.getValue());
            }
        }
        
        
        if (config.endpointRequiresAuthentication()) {
            String authString = config.getEndpointUsername() + ":" + config.getEndpointPassword();

            // The only public Base64 encoder that exist in Java 7 and before is from the
            // JAXB package so we will (mis)use that here for Base64 encoding.        
            // Note: Java 8 will finally have a Base64 class in the Util package.
            String authStringEnc = javax.xml.bind.DatatypeConverter.printBase64Binary(authString.getBytes());
            httpUrlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
        }
        
        return httpUrlConnection;
    }
    
    
    /**
     * Utility function to calculate the total number of bytes in 
     * an array of Strings. This method assumes that the strings
     * only contain single-byte characters (i.e. 1 char = 1 byte).
     * @param arr
     * @return total number of bytes
     */
    private static int getBytesSize(ArrayList<String> arr) {
        int count=0;
        
        for (String str : arr) {
            count = count + str.length();
        }
        return count;
    }
    
    /**
     * Utility method
     */
    private static int notifyProgress(UploadProgress notifier, File file, int bytesWritten, int bytesTotal, int prevPct) {
        if (notifier != null) {
            int pct = (int) ((bytesWritten * 100L) / bytesTotal);
            
            // We don't want to inform about the same pct twice
            if (prevPct != pct) {
                notifier.uploadProgress(file, bytesTotal, pct);
                return pct;
            }
            return prevPct;
        }
        return prevPct;
    }
    
    
    /**
     * Helper class. Only used internally.
     */
    private static class UploadFile  {
        private final UploadItem uploadItem;
        private final ArrayList<String> mpFooters;
        private final ArrayList<String> mpHeaders;
        private final String formFieldName;

        public UploadFile(String formFieldName, UploadItem uploadItem, ArrayList<String> mpHeaders, ArrayList<String> mpFooters) {
            this.uploadItem = uploadItem;
            this.mpHeaders = mpHeaders;
            this.mpFooters = mpFooters;
            this.formFieldName = formFieldName;
        }

        public UploadItem getUploadItem() {
            return uploadItem;
        }

        public ArrayList<String> getMpFooters() {
            return mpFooters;
        }

        public ArrayList<String> getMpHeaders() {
            return mpHeaders;
        }

        public String getFormFieldName() {
            return formFieldName;
        }
        
        public long  getTotalByteSize() {
            return HttpsFileUploader.getBytesSize(mpHeaders)  
                    + uploadItem.getSizeInBytes()
                    + HttpsFileUploader.getBytesSize(mpFooters) ;
        }
    }
    

    /**
     * Helper class. Only used internally.
     */
    private static class UploadOtherField  {
        private final ArrayList<String> mpFooters;
        private final ArrayList<String> mpHeaders;
        private final String formFieldName;
        private final String fieldValue;

        public UploadOtherField(String formFieldName, String fieldValue, ArrayList<String> mpHeaders, ArrayList<String> mpFooters) {
            this.mpHeaders = mpHeaders;
            this.mpFooters = mpFooters;
            this.formFieldName = formFieldName;
            this.fieldValue = (fieldValue == null) ? "" : fieldValue;
        }

        public ArrayList<String> getMpFooters() {
            return mpFooters;
        }

        public ArrayList<String> getMpHeaders() {
            return mpHeaders;
        }

        public String getFormFieldName() {
            return formFieldName;
        }

        public String getFieldValue() {
            return fieldValue;
        }

        public long  getTotalByteSize() {
            return HttpsFileUploader.getBytesSize(mpHeaders)  
                    + fieldValue.length()
                    + HttpsFileUploader.getBytesSize(mpFooters) ;
        }
    }
    
}
