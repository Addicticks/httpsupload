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

/**
 *
 */
public class Utils {
    
    private static final BigDecimal BDEC_KB = new BigDecimal(1024L);
    private static final BigDecimal BDEC_MB = new BigDecimal(1048576L);
    private static final BigDecimal BDEC_GB = new BigDecimal(1073741824L);
    
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
     * speaking this incorrect but this is what most operating systems do as well.
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
     * Strips a string from HTML and replaces {@code <br>},{@code <p>},
     * {@code <h1>}, {@code <h2>} and {@code <h3>} with new line. This produces
     * a string which is readable as plain text.
     *
     * <p>
     * Note: The method is simple and crude. It will not work on malformed HTML
     * or if there are un-escaped {@code <} or {@code >} outside of the HTML
     * tags. However, for all but the most complex use cases, the method will
     * probably work just fine.
     *
     * @param inp
     * @return
     */
    public static String stripHtml(String inp) {
        boolean intag = false;
        boolean tagHasEnded = false;
        StringBuilder outp = new StringBuilder();
        StringBuilder tagName = new StringBuilder();
        boolean ignoreContents = false;

        for (int i = 0; i < inp.length(); i++) {
            char ch = inp.charAt(i);
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
            if (intag && inp.charAt(i) == '>') {
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
}
