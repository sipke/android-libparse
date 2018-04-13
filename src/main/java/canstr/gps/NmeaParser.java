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

package canstr.gps;

import java.util.ArrayList;
import java.util.List;

import canstr.file.FileLogger;
import canstr.parse.IPacket;
import canstr.parse.SentenceParser;
import es.agroguia.model.NMEA;

public class NmeaParser extends SentenceParser {

    public NmeaParser() {
        super();
        nNmea = new NMEA();
    }

    @Override
    public void onPacket(IPacket packet)
    {
        NMEA.GPSPosition gps;
        String line = packet.getString().trim();
        LogToFile(line);
        gps = nNmea.parse(line);
        if (gps != null) {
            for (GpsListener listener : gpsListeners) {
                listener.onGpsPosition(gps);
            }
        }
    }

    public void addGpsListener(GpsListener listener) {
        gpsListeners.add(listener);
    }

    public interface GpsListener {
        void onGpsPosition(NMEA.GPSPosition gps);
    }

    public boolean isLogging() {
        return mLogging;
    }
    public void stopLogging() {
        mLogging = false;
        mLogger = null;
    }
    /*
     * Toggle the logging mode. If previously logging, stop logging, otherwise start logging
     * to filename prepended with a date. If a failure occurs starting to log, return false.
     */
    public boolean ToggleLog(String path, String filename) {
        mLogging = !mLogging;
        if (mLogging) {
            if (mLogger == null) {
                mLogger = new FileLogger(path, filename);
            } else {
                mLogger.NewFile(path, filename);
            }
        }
        return mLogging;
    }

    /*
     * Get the filename for the log currently being written to
     * Returns null if no logging in progress.
     */
    public String getLogFileName() {
        String file = null;
        if (mLogger != null) {
            file = mLogger.getFileName();
        }
        return file;
    }

    private void LogToFile(String data)
    {
        try {
            if (mLogging) {
                data = data +"\r";
                mLogger.appendLog(data);
            }
        } catch (Exception ex) {}
    }

    private FileLogger mLogger;
    private boolean mLogging;

    private NMEA nNmea;
    private List<GpsListener> gpsListeners = new ArrayList<GpsListener>();
}

