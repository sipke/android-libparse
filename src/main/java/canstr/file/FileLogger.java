/*
 * Copyright (c) 2017. Sipke Vriend
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of canstr nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package canstr.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple class for logging to a file.
 * Each append will open, write, and close the file.
 * The path and filename can be modified and will result in the next append being to the new file.
 */
public class FileLogger {
    private String mFileName;
    private final Object mFileLock = new Object();

    /**
     * Construct a new FileLogger for a file at path/fileName location.
     * @param path Directory which will be created to store the log file.
     * @param baseName Base name of the file which will be prefixed with a timestamp
     */
    public FileLogger(String path, String baseName)
    {
        mFileName = NewFile(path, baseName);
    }

    /**
     * Set a new path and filename to be used by the next append.
     * @param path Directory which will be created to store the log file.
     * @param baseName Base name of the file which will be prefixed with a timestamp
     * @return
     */
    public String NewFile(String path, String baseName)
    {
        synchronized (mFileLock) {
            mFileName = path + "/" + FormattedFileName(baseName);
        }
        return mFileName;
    }

    /**
     * @return Filename being logged to
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     * Append the given text to the file.
     * The file is opened, appended to and closed.
     *
     * @param text The text to append to the file.
     */
    public void appendLog(String text)
    {
        synchronized (mFileLock) {
            File logFile = new File(mFileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.flush();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String FormattedFileName(String fileName)
    {
        String newName = new SimpleDateFormat("yyyyMMdd_HHmmss-'" + fileName + "'", Locale.US).format(new Date());
        return newName;
    }
}
