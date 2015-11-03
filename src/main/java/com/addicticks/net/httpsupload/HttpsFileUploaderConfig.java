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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Configuration values for the connection to
 * a HTTP or HTTPS server. 
 * 
 * @author Addicticks 
 */
public class HttpsFileUploaderConfig {
    
    private static final String[] RESTRICTED_HTTP_HEADERS = new String[]{"Connection","Cache-Control","Content-Type","Content-Length","Authorization"};
    
    private String endpointUsername=null;
    private String endpointPassword;
    private boolean validateCertificates=true;
    private String[] acceptedIssuers = null;
    private String proxyAddress=null;
    private int proxyPort=8080;
    private final URL url;
    private Map<String,String> additionalHeaders;
    private int connectTimeoutMs = CONNECT_TIMEOUT_MS;
    private int readTimeoutMs = READ_TIMEOUT_MS;
    
    /**
     * Default connect timeout on the URL connection.
     * (value in milliseconds)
     */
    public static final int CONNECT_TIMEOUT_MS = 10000;
    /**
     * Default read timeout on the URL connection. This is the time from the
     * point where all the data in the HTTP POST message has been sent to the
     * server and to the point where the server replies. (value in milliseconds)
     */
    public static final int READ_TIMEOUT_MS = 5000;
    

    
    
    /**
     * Creates a configuration object with only the URL endpoint defined. The following
     * defaults apply:<p>
     * <br>
     * <ul>
     * <li>No use of authentication on endpoint</li>
     * <li>No use of proxy</li>
     * <li>Certificates from the endpoint are validated</li>
     * </ul>
     * The defaults can be changed by subsequent calls to the setter methods.
     * @param url Sets the URL of the endpoint. This is expected to start with
     * either a HTTP or a HTTPS url. The URL can optionally include a port 
     * number.
     */
    public HttpsFileUploaderConfig(URL url)  {
        this.url = url;
    }
    
    
    /**
     * Gets the endpoint authentication username (authentication type: Basic).
     * Returns <code>null</code> if the {@link #setEndpointUsername(java.lang.String)}
     * hasn't been called. A value of <code>null</code> indicates that the
     * endpoint does not require authentication.
     *
     * @return username to use for authentication
     */
    public String getEndpointUsername() {
        return endpointUsername;
    }

    /**
     * Sets the username for authentication against the endpoint. Do not set this
     * if the endpoint does not require authentication. Only authentication of
     * type "Basic" is currently supported.
     * @param endpointUsername username to use for authentication
     */
    public void setEndpointUsername(String endpointUsername) {
        this.endpointUsername = endpointUsername;
    }

    /**
     * Gets the endpoint authentication password (authentication type: Basic). 
     * Returns <code>null</code>
     * if the {@link #setEndpointPassword(java.lang.String) } hasn't been called.
     * A value of <code>null</code> indicates that the endpoint does not require
     * authentication.
     * @return un-encrypted password.
     */
    public String getEndpointPassword() {
        return endpointPassword;
    }

    /**
     * Sets the un-encrypted password for authentication against the endpoint. Do not set this
     * if the endpoint does not require authentication. Only authentication of
     * type "Basic" is currently supported.
     * @param endpointPassword un-encrypted password 
     */
    public void setEndpointPassword(String endpointPassword) {
        this.endpointPassword = endpointPassword;
    }

    /**
     * Must certificates from the endpoint be validated?
     * @return 
     */
    public boolean isValidateCertificates() {
        return validateCertificates;
    }

    /**
     * Should certificates from the endpoint be validated ? Default is 
     * <code>true</code>. Set to <code>false</code> if the endpoint is trusted but
     * is using a self-signed certificate.
     * @param validateCertificates 
     */
    public void setValidateCertificates(boolean validateCertificates) {
        this.validateCertificates = validateCertificates;
    }



    /**
     * Address of proxy if one has been defined. A value of
     * <code>null</code> indicates no use of proxy. 
     * @see #setProxyAddress(java.lang.String) 
     * @return 
     */
    public String getProxyAddress() {
        return proxyAddress;
    }

    /**
     * Sets the address of the proxy. If the connection does not need to
     * go through a proxy server in order to reach its endpoint then do not use
     * this method. 
     * @param proxyAddress either a string hostname or an IP addresss. Must <i>
     * not</i> include a port number.
     */
    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    /**
     * Gets the port number used by the proxy.
     * @see #setProxyPort(int) 
     * @return 
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Sets the port of the proxy. If the connection does not need to
     * go through a proxy server in order to reach its endpoint then do not use
     * this method. 
     * @param proxyPort port number, a value between 1 and 65535. Typically 
     * HTTP/HTTPS proxies use port 8080 by convention.
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    /**
     * Does the connection go through a proxy server?
     * @return 
     */
    public boolean usesProxy() {
        if (this.proxyAddress == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Does the endpoint require authentication?
     * @return 
     */
    public boolean endpointRequiresAuthentication() {
        if (this.endpointUsername == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Gets the <code>endpointURLString</code> as a URL object.
     * @return 
     */
    public URL getURL() {
        return this.url;
    }

    
    /**
     * Gets the connect timeout that is used on the URL connection.
     * (in milliseconds). Defaults to {@link #CONNECT_TIMEOUT_MS} if
     * not set explicitly.
     * @return timeout in milliseconds
     */
    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    /**
     * Sets the connect timeout that is used on the URL connection.
     * (in milliseconds). Defaults to {@link #CONNECT_TIMEOUT_MS} if
     * not set explicitly.
     * @param connectTimeoutMs connect timeout in milliseconds
     */
    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    /**
     * Gets the read timeout that is used on the URL connection.
     * (in milliseconds). Defaults to {@link #READ_TIMEOUT_MS} if
     * not set explicitly.
     * @return timeout in milliseconds
     */
    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    /**
     * Gets the read timeout that is used on the URL connection.
     * (in milliseconds). Defaults to {@link #READ_TIMEOUT_MS} if
     * not set explicitly.
     * @param readTimeoutMs read timeout in milliseconds
     */
    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    /**
     * Gets list of accepted Issuer Organization names that will be
     * accepted for the server's certificate.
     * 
     * @see #setAcceptedIssuers(java.lang.String[]) 
     * @return the list of accepted Issuer Organization names that will be
     * accepted for the server's certificate or {@code null} if all
     * Issuer Organization names are accepted.
     */
    public String[] getAcceptedIssuers() {
        return acceptedIssuers;
    }

    /**
     * Sets the list of Issuer Organization names that will be accepted for
     * the server's certificate.
     * 
     * <p>This feature only applies when {@link #isValidateCertificates()} is 
     * {@code false}. 
     * 
     * <p>The purpose of the feature is to provide some minimal security
     * rather than simply accepting any certificate. If - for example - you create
     * a self-signed certificate then you decide the 'issuer organization' 
     * yourself and the issuer organization name you used when you created the
     * certificate can then be validated here. 
     * 
     * @param acceptedIssuers accept only a certificate from one of these issuer
     * organizations. Check is done against the Organization (O) field in the 'Issued
     * By' section of the server's certificate. A {@code null} means all issuer 
     * organizations are accepted.
     */
    public void setAcceptedIssuers(String[] acceptedIssuers) {
        this.acceptedIssuers = acceptedIssuers;
    }

    
    /**
     * Gets the additional HTTP headers used on the upload POST request.
     * 
     * @see #setAdditionalHeaders(java.util.Map) 
     * @return 
     */
    public Map<String,String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    
    /**
     * Sets additional overall HTTP headers to add to the upload POST request. There's 
     * rarely a need to use this method.
     * 
     * <p>The following header fields are automatically set:
     * <pre>
     *    "Connection"
     *    "Cache-Control"
     *    "Content-Type"
     *    "Content-Length"
     *    "Authorization"
     * </pre> and you must <i>never</i> set these here. (if you do they will be
     * ignored)
     * 
     * <p>However you might want to use this method to 
     * explicitly set e.g. {@code User-Agent} or non-standard header fields that
     * are required for your particular endpoint. For example by overriding
     * {@code User-Agent} you can make the upload operation look to the 
     * endpoint as if it comes from a browser.
     * 
     * @param additionalHeaders Map of HTTP request headers. The key is the header field
     *    name and the value is the header field value.
     */
    public void setAdditionalHeaders(Map<String,String> additionalHeaders) {
        
        Map<String, String> newMap = new HashMap<>();
        for (Entry<String,String> e : additionalHeaders.entrySet()) {
            boolean found = false;
            for(String restrictedHeaderField : RESTRICTED_HTTP_HEADERS) {
                if (e.getKey().equalsIgnoreCase(restrictedHeaderField)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newMap.put(e.getKey(), e.getValue());
            }
        }
        this.additionalHeaders = newMap;
    }
    
    
    
}
