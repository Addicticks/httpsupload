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

import java.net.HttpURLConnection;

/**
 * Enclosing class for the result of a file upload operation.
 * 
 * @author Addicticks 
 */
public class HttpsFileUploaderResult {
    private final int httpStatusCode;
    private final String responseText;

    protected  HttpsFileUploaderResult(int httpStatusCode, String responseText) {
        this.httpStatusCode = httpStatusCode;
        this.responseText = responseText;
    }
    
    /**
     * Gets the HTTP response status code. For a successful upload this
     * should be equal to {@link java.net.HttpURLConnection#HTTP_OK}.
     * 
     * @see #isError() 
     * @return status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets the response text from an upload request. This is the text
     * (typically some HTML) that the server responds. In the context of file
     * upload it is rarely useful. Instead the HTTP status code should be used
     * to verify if an upload has been successful.
     *
     * @return server response
     */
    public String getResponseText() {
        return responseText;
    }
 
    /**
     * Gets the response text from an upload request. Same as
     * {@link #getResponseText()} but all HTML has been stripped from the text
     * and {@code <br>}, {@code <p>}, {@code <h1>}, {@code <h2>} and {@code <h3>}
     * has been replaced with newline characters for readability.
     *
     * @return server response
     */
    public String getResponseTextNoHtml() {
        return Utils.stripHtml(responseText);
    }
    
    /**
     * Convenience method. Returns true if {@link #getHttpStatusCode() }
     * is anything other than {@link java.net.HttpURLConnection#HTTP_OK}.
     * @return 
     */
    public boolean isError() {
        return (httpStatusCode != HttpURLConnection.HTTP_OK);
    }
}
