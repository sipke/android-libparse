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

package canstr.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
 * Class which reads a file (line by line) in a thread and streams the data to callback
 * onDataReceived() method.
 * Each line in the file should be a packet. The line ending in the file is not used, instead
 * the user of this class needs to specify what delimiter they wish to add to the packet.
 */
public class FileDataStreamer {

    public FileDataStreamer () {
    }

    public FileDataStreamer(String filename, int cadence_ms, String delimiter) {
        mFileName = filename;
        mCadence = cadence_ms;
        if (cadence_ms < 0) {
            mCadence = 30;
        }
        mDelimeter = delimiter;
        if (delimiter == null) {
            mDelimeter = "";
        }
    }

    /**
     * method called when a line has been read.
     * @param data content of file line with delimeter set at constructor appended.
     */
    public void onReceivedData(byte[] data) {
    }

    public boolean start() throws FileNotFoundException, IOException {
        final File file = _openFile(mFileName);
        boolean started = false;
        if (file != null) {
            final FileReader fileReader = new FileReader(file);
            final BufferedReader br = new BufferedReader(fileReader);
            mReaderThread = new Thread() {
                public void run() {
                    try {
                        String line = null;

                        /*
                         * Read line by line and call onReceiveData at set cadence.
                         */
                        boolean running = mRunning = true;
                        do {
                            try {
                                line = br.readLine();
                                if (line != null) {
                                    String packet = line + mDelimeter;
                                    byte[] data = packet.getBytes();
                                    onReceivedData(data);
                                    TimeUnit.MILLISECONDS.sleep(mCadence);
                                }
                                synchronized (mLockRunning) {
                                    running = mRunning;
                                }
                            } catch (IOException ex) {
                                String ioex = "";
                            }
                        } while (running && (line != null));
                        br.close();
                    // Silently ignore exceptions until such time that we add an error callback.
                    } catch (InterruptedException ex) {
                        String uiex = "";
                    } catch (Exception e) {
                        String es = "";
                    }
                }
            };
            mReaderThread.start();
            started = true;
        }
        return started;
    }

    public void stop() {
        synchronized (mLockRunning) {
            mRunning = false;
        }
    }

    private File _openFile(String filename) {
        File file = null;
        if (filename != null) {
            file = new File(filename);
        }
        return file;
    }

    private String mFileName;
    private int mCadence;
    private String mDelimeter;
    private Thread mReaderThread;
    private boolean mRunning;
    final private Object mLockRunning = new Object();
}
