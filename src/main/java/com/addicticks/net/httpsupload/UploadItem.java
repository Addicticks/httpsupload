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
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author addicticks
 */
public abstract class UploadItem {

    private final String mimeType;
    private final String hintFilename;

    public UploadItem(String hintFilename, String mimeType) {
        this.mimeType = mimeType;
        this.hintFilename = hintFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getHintFilename() {
        return hintFilename;
    }

    public abstract InputStream getInputStream() throws IOException;
    
    /**
     * Gets the byte size of the upload item or -1 if the size is unknown.
     * @return 
     */
    public abstract long getSizeInBytes();
    
    /**
     * Gets the file to upload or {@code null} if what is being uploaded
     * is not a physical file.
     * @return 
     */
    public abstract File getFile();

}
