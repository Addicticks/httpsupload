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
 * Stream based upload item.
 * @author addicticks
 */
public class UploadItemStream extends UploadItem {
    
    private final InputStream is;

    /**
     * Constructs a a stream based upload specification.
     *
     * @param is data stream to upload
     * @param hintFilename hint given to the server about what filename to use
     * for the file.
     * @param mimeType MIME type for the file, e.g.
     * <code>application/zip</code>. See
     * <a href="http://en.wikipedia.org/wiki/Internet_media_type">Wikipedia</a>
     * for more information.
     */
    public UploadItemStream(InputStream is, String hintFilename, String mimeType) {
        super(hintFilename, mimeType);
        this.is = is;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return is;
    }

    @Override
    public long getSizeInBytes() {
        return -1;
    }

    @Override
    public File getFile() {
        return null;
    }
    
}
