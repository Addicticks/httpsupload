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

import java.io.File;

/**
 * Gives information about file upload progress. See 
 * {@link HttpsFileUploader}.
 * 
 * <p>Note that it is perfectly possible to receive the callbacks in
 * this class even if the upload is failing. Therefore it is important
 * that the result of the upload operation, {@link HttpsFileUploaderResult}, is
 * always checked.
 * 
 * @author Addicticks 
 */
public interface FileUploadProgress {
    
    /**
     * Informs that an upload operation is about to begin. An upload
     * operation may include multiple files ({@code noOfFiles > 1}.
     * 
     * @param noOfFiles the number of files that will be uploaded in this
     * upload operation. 
     * @param totalSizeAll the total number of file bytes that will be sent
     * in this upload operation (the sum of the size of all files in the 
     * upload operation)
     */
    public void uploadStart(int noOfFiles, long totalSizeAll);
    
    
    /**
     * Informs about file upload progress for a given file. 
     * 
     * <p>
     * The same percentage will never be reported twice. For each file
     * being uploaded there will be between 2 (one for "0%" and one for "100%")
     * and maximum 101 invocations of this method. There will always be
     * a "0%" and a "100%" invocation. The "0%" invocation can effectively
     * be interpreted as a file is <i>about</i> to be uploaded while the
     * "100%" invocation can be interpreted as the the upload for that file
     * has been completed.
     * 
     * <p>
     * Regarding multi-file uploads: Files are uploaded sequentially and in the
     * order they are specified in the
     * {@link HttpsFileUploader#uploadFile(com.addicticks.net.httpsupload.HttpsFileUploaderConfig, java.util.Map, java.util.Map, com.addicticks.net.httpsupload.FileUploadProgress) uploadFiles map}.
     * This callback is about a single of each such file upload. Therefore the 
     * values in {@code totalSize} and {@code pct} relate only to this single
     * file upload and not the whole upload operation.
     * 
     * <p>
     * For very small files (below 8K) there will only be a "0%" and a "100%"
     * invocation as such file is uploaded in a single write operation.
     *
     * @param file local file currently being uploaded
     * @param totalSize total number of bytes to be sent in the upload (size in
     * bytes of the current file being uploaded)
     * @param pct upload progress in percentage. Value between 0 and 100.
     */
    public void uploadProgress(File file, long totalSize, int pct);
    

    /**
     * Informs that the upload operation has completed.
     * @param totalSizeAll the total number of file bytes that was sent
     * in this upload operation (the sum of the size of all files in the 
     * upload operation)
     * @param msecondsUsed number of milliseconds used on the file upload
     * operation
     */
    public void uploadEnd(long totalSizeAll, long msecondsUsed);

}
