/*
 * Copyright 2015 Addicticks.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * File upload item.
 * 
 * <p>Use this when what you're uploading is a file.
 * 
 * @author addicticks
 */
public class UploadItemFile extends UploadItem {

    private final File file;

    /**
     * Constructs a file upload specification.
     *
     * @param file file to upload.
     * @param hintFilename hint given to the server about what filename to use
     * for the file.
     * @param mimeType MIME type for the file, e.g.
     * <code>application/zip</code>. See
     * <a href="http://en.wikipedia.org/wiki/Internet_media_type">Wikipedia</a>
     * for more information.
     */
    public UploadItemFile(File file, String hintFilename, String mimeType) {
        super(
                (hintFilename == null) ? file.getName() : hintFilename,
                (mimeType == null) ? URLConnection.guessContentTypeFromName(file.getAbsolutePath()) : mimeType
        );
        this.file = file;
    }

    /**
     * Constructs a file upload specification.
     *
     * <p>
     * <code>hintFilename</code> will be derived from <code>
     * file</code> argument.
     * <p>
     * <code>mimeType</code> will be guessed from <code>
     * file</code> argument using {@link java.net.URLConnection#guessContentTypeFromName(java.lang.String)
     * }.
     *
     *
     * @param file file to upload.
     */
    public UploadItemFile(File file) {
        this(file, file.getName(), URLConnection.guessContentTypeFromName(file.getAbsolutePath()));
    }

    /**
     * Constructs a file upload specification.
     *
     * <p>
     * <code>hintFilename</code> will be derived from <code>
     * file</code> argument.
     *
     *
     * @param file file to upload.
     * @param hintFilename hint given to the server about what filename to use
     * for the file.
     */
    public UploadItemFile(File file, String hintFilename) {
        this(file, hintFilename, URLConnection.guessContentTypeFromName(file.getAbsolutePath()));
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    protected InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    @Override
    public long getSizeInBytes() {
        return file.length();
    }

}
