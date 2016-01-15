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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods.
 */
public class Utils {
    
    private static final BigDecimal BDEC_KB = new BigDecimal(1024L);
    private static final BigDecimal BDEC_MB = new BigDecimal(1048576L);
    private static final BigDecimal BDEC_GB = new BigDecimal(1073741824L);


    /**
     * HTTP Response Status code (with text explanation)
     * 
     * <p>These are similar to the {@code HTTP-}codes found in {@link java.net.HttpURLConnection}
     * however here they are represented as an {@code enum} and come with text as well.
     * Also a few status codes that were missing in the JDK have been added (such as
     * {@code HTTP_CONTINUE}).
     * 
     */
    public enum HttpStatusCode {
        /*
         * Justification: Unfortunately such a list - with accompanying text -
         * doesn't exist in the JDK. Apache HttpComponents has this but we do not
         * want to bring in that library solely for this purpose. 
         */
        
        /**
         * 100 Continue
         */
        HTTP_CONTINUE(100, "Continue "),
        /**
         * 101 Switching Protocols
         */
        HTTP_SWITCHING_PROTOCOLS(101, "Switching Protocols "),
        /**
         * 102 Processing
         */
        HTTP_PROCESSING(102, "Processing "),
        /**
         * 200 OK
         */
        HTTP_OK(200, "OK "),
        /**
         * 201 Created
         */
        HTTP_CREATED(201, "Created "),
        /**
         * 202 Accepted
         */
        HTTP_ACCEPTED(202, "Accepted "),
        /**
         * 203 Non Authoritative Information
         */
        HTTP_NON_AUTHORITATIVE_INFORMATION(203, "Non Authoritative Information "),
        /**
         * 204 No Content
         */
        HTTP_NO_CONTENT(204, "No Content "),
        /**
         * 205 Reset Content
         */
        HTTP_RESET_CONTENT(205, "Reset Content "),
        /**
         * 206 Partial Content
         */
        HTTP_PARTIAL_CONTENT(206, "Partial Content "),
        /**
         * 207 Multi-Status or Partial Update OK
         */
        HTTP_MULTI_STATUS(207, "Multi-Status  or Partial Update OK"),
        /**
         * 300 Multiple Choices
         */
        HTTP_MULTIPLE_CHOICES(300, "Multiple Choices "),
        /**
         * 301 Moved Permanently
         */
        HTTP_MOVED_PERMANENTLY(301, "Moved Permanently "),
        /**
         * 302 Moved Temporarily
         */
        HTTP_MOVED_TEMPORARILY(302, "Moved Temporarily  "),
        /**
         * 303 See Other
         */
        HTTP_SEE_OTHER(303, "See Other "),
        /**
         * 304 Not Modified
         */
        HTTP_NOT_MODIFIED(304, "Not Modified "),
        /**
         * 305 Use Proxy
         */
        HTTP_USE_PROXY(305, "Use Proxy "),
        /**
         * 307 Temporary Redirect
         */
        HTTP_TEMPORARY_REDIRECT(307, "Temporary Redirect "),
        /**
         * 400 Bad Request
         */
        HTTP_BAD_REQUEST(400, "Bad Request "),
        /**
         * 401 Unauthorized
         */
        HTTP_UNAUTHORIZED(401, "Unauthorized "),
        /**
         * 402 Payment Required
         */
        HTTP_PAYMENT_REQUIRED(402, "Payment Required "),
        /**
         * 403 Forbidden
         */
        HTTP_FORBIDDEN(403, "Forbidden "),
        /**
         * 404 Not Found
         */
        HTTP_NOT_FOUND(404, "Not Found "),
        /**
         * 405 Method Not Allowed
         */
        HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed "),
        /**
         * 406 Not Acceptable
         */
        HTTP_NOT_ACCEPTABLE(406, "Not Acceptable "),
        /**
         * 407 Proxy Authentication Required
         */
        HTTP_PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required "),
        /**
         * 408 Request Timeout
         */
        HTTP_REQUEST_TIMEOUT(408, "Request Timeout"),
        /**
         * 409 Conflict
         */
        HTTP_CONFLICT(409, "Conflict "),
        /**
         * 410 Gone
         */
        HTTP_GONE(410, "Gone "),
        /**
         * 411 Length Required
         */
        HTTP_LENGTH_REQUIRED(411, "Length Required "),
        /**
         * 412 Precondition Failed
         */
        HTTP_PRECONDITION_FAILED(412, "Precondition Failed "),
        /**
         * 413 Request Entity Too Large
         */
        HTTP_REQUEST_TOO_LONG(413, "Request Entity Too Large"),
        /**
         * 414 Request-URI Too Long
         */
        HTTP_REQUEST_URI_TOO_LONG(414, "Request-URI Too Long "),
        /**
         * 415 Unsupported Media Type
         */
        HTTP_UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type "),
        /**
         * 416 Requested Range Not Satisfiable
         */
        HTTP_REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable "),
        /**
         * 417 Expectation Failed
         */
        HTTP_EXPECTATION_FAILED(417, "Expectation Failed "),
        /**
         * 419 Insufficient Space on Resource
         */
        HTTP_INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space on Resource"),
        /**
         * 420 Method Failure
         */
        HTTP_METHOD_FAILURE(420, "Method Failure"),
        /**
         * 422 Unprocessable Entity
         */
        HTTP_UNPROCESSABLE_ENTITY(422, "Unprocessable Entity "),
        /**
         * 423 Locked
         */
        HTTP_LOCKED(423, "Locked "),
        /**
         * 424 Failed Dependency
         */
        HTTP_FAILED_DEPENDENCY(424, "Failed Dependency "),
        /**
         * 500 Server Error
         */
        HTTP_INTERNAL_SERVER_ERROR(500, "Server Error "),
        /**
         * 501 Not Implemented
         */
        HTTP_NOT_IMPLEMENTED(501, "Not Implemented "),
        /**
         * 502 Bad Gateway
         */
        HTTP_BAD_GATEWAY(502, "Bad Gateway "),
        /**
         * 503 Service Unavailable
         */
        HTTP_SERVICE_UNAVAILABLE(503, "Service Unavailable "),
        /**
         * 504 Gateway Timeout
         */
        HTTP_GATEWAY_TIMEOUT(504, "Gateway Timeout "),
        /**
         * 505 HTTP Version Not Supported
         */
        HTTP_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported "),
        /**
         * 507 Insufficient Storage
         */
        HTTP_INSUFFICIENT_STORAGE(507, "Insufficient Storage ");
        private final int sc;
        private final String txt;
        private static final Map<Integer, HttpStatusCode> map = new HashMap<>();
    
        HttpStatusCode(int sc, String txt) {
            this.sc = sc;
            this.txt = txt;
        }
        
        static {
            for (HttpStatusCode sCode : HttpStatusCode.values()) {
                if (map.put(sCode.sc, sCode) != null) {
                    // Just a warning to the maintainer of this library.
                    throw new RuntimeException("Status code " + sCode.sc + " already exists in this enum. Please remove duplicate");
                }
            }
        }

        /**
         * Gets the numerical status code.
         * @return 
         */
        public int getStatusCode() {
            return sc;
        }

        
        
        /**
         * Gets a one-line short text for the status code.
         * @return 
         */
        public String getText() {
            return txt;
        }
        
        /**
         * Gets an enumeration value from a numeric HTTP status code.
         * @param sc status code
         * @return enumeration value or {@code null} if the provided status
         *    code is unknown to this enumeration.
         */
        public static HttpStatusCode fromNumStatusCode(int sc) {
            return map.get(sc);
        }

        /**
         * Gets a string representation, the numerical status
         * code concatenated with the status code text.
         * @return 
         */
        @Override
        public String toString() {
            return sc + " " + txt;
        }
        
    }

    
    private Utils() {
    }
    
    /**
     * Formats a byte size (for example the size of a file) into an easy-to-read
     * string. It is somewhat similar to the {@code -h} option which exist on
     * many Unix/Linux commands. Readability is favored over precision.
     * 
     * <p>
     * Note that the method uses the abbreviations "KB", "MB" and "GB" to denote
     * 1024 bytes, 1024<sup>2</sup> bytes and 1024<sup>3</sup> bytes. Strictly 
     * speaking this is incorrect but this is what most operating systems do as well.
     * 
     * <p>
     * Rules are as follows:<br>
     * If the size is &lt;1 KB : Will be shown as-is. Example returned string:
     * <code>"361 bytes"</code><br>
     * If the size is &gt;= 1 KB and &lt; 1 MB : Will be shown as KB value with
     * no decimals. Example returned string: <code>"76 KB"</code><br>
     * If the size is &gt;= 1 MB and &lt; 1 GB : Will be shown as MB value with
     * 1 decimal. Example returned string: <code>"323.4 MB"</code><br>
     * If the size is &gt;= 1 GB : Will be shown as GB value with 2 decimal.
     * Example returned string: <code>"1.42 GB"</code>
     *
     * <p>
     * Except for input values above 1 TB the returned string will never be more
     * than 9 bytes in length.
     *
     * @param size byte size 
     * @return formatted string
     */
    public static String fileSizeAsStr(long size) {
        
        BigDecimal bd = BigDecimal.valueOf(size);
        if (size < 1024) {
            if (size == 1) {
                return "1 byte";
            } else {
                return size + " bytes";
            }
        }
        if (size < 1048576) {   // 1 MB
            return bd.divide(BDEC_KB, 0 , RoundingMode.HALF_UP).toPlainString() + " KB";
        }
        if (size < 1073741824) {   // 1 GB
            return bd.divide(BDEC_MB, 1 , RoundingMode.HALF_UP).toPlainString() + " MB";
        }
        return bd.divide(BDEC_GB, 2 , RoundingMode.HALF_UP).toPlainString() + " GB";
    }

    /**
     * Strips a string from HTML markup. Elements {@code <br>},{@code <p>}, {@code <h1>}, 
     * {@code <h2>} and {@code <h3>} are replaced with with new line. This produces
     * a string which is readable as plain text.
     *
     * <p>
     * Note: The method is simple and crude. It will not work on malformed HTML
     * or if there are un-escaped {@code <} or {@code >} outside of the HTML
     * tags. However, for all but the most complex use cases, the method will
     * probably work just fine.
     *
     * @param str
     * @return
     */
    public static String stripHtml(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return str;
        }
        boolean intag = false;
        boolean tagHasEnded = false;
        StringBuilder outp = new StringBuilder();
        StringBuilder tagName = new StringBuilder();
        boolean ignoreContents = false;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!intag && ch == '<') {
                intag = true;
                
                // Note: In HTML the tag name must follow *immediately*
                // after the opening '<' character. Therefore the tagName starts
                // now.
                tagHasEnded = false;
                continue;
            }
            
            if (intag) {
                if (ch == '/'  || ch == ' ' || ch == '>') {
                    tagHasEnded = true;
                }
                if (!tagHasEnded) {
                    tagName.append(ch);
                }
            }
            if (intag && str.charAt(i) == '>') {
                intag = false;
                String tName = tagName.toString();
                if (tName.equalsIgnoreCase("br")
                        || tName.equalsIgnoreCase("p")
                        || tName.equalsIgnoreCase("h1")
                        || tName.equalsIgnoreCase("h2")
                        || tName.equalsIgnoreCase("h3")) {
                    outp.append(System.lineSeparator());
                }
                ignoreContents = tName.equalsIgnoreCase("style");  // contents inside <style> tag is ignored
                tagName.setLength(0);  // clear the contents
                continue;
            }
            if (!intag && (!ignoreContents)) {
                outp = outp.append(ch);
            }
        }
        return outp.toString();
    }
    
    /**
     * Gets the numerical HTTP status code concatenated with the
     * text representing that status code, for example {@code "403 Forbidden"}.
     * 
     * <p>If the status code is unknown then the text will be 
     * {@code "<Unknown status code>"}.
     * @param sc numerical HTTP status code
     * @return 
     */
    public static String getHttpStatusCodeText(int sc) {
        HttpStatusCode sCode = HttpStatusCode.fromNumStatusCode(sc);
        if (sCode == null) {
            return sc + " <unknown status code>";
        } else {
            return sCode.toString();
        }
    }
}
