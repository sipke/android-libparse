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
 *   * Neither the name of Redis nor the names of its contributors may be used
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

package canstr.parse;

import java.io.UnsupportedEncodingException;

/**
 * Class which implements IParse to parse a byte stream and extract sentences delimited by the
 * given delimiter.
 */
public class SentenceParser implements IParse {
    /**
     * The default sentence parser sets
     * maxSentenceSize of 2048, delimeter of '\r' which tends to be the more common in serial
     * protocols and utf8 encoding.
     */
    public SentenceParser() {
        this(2048, "\r");
    }

    /**
     *
     * @param maxSentenceSize If no delimiter is received before byte size received,
     *                        all data to date is discarded.
     * @param delimiter The byte which delimits packets
     */
    public SentenceParser(int maxSentenceSize, String delimiter) {
        mDelimiter = delimiter;
        mPacket = new StreamPacket(maxSentenceSize);
    }

    /**
     * Append the data to previously received data and parse for the sentence delimiter.
     * On finding a sentence, call onPacket so the inheriting class can overwrite it and deal with
     * the actual packet.
     * @param data new data to append to any existing data
     */
    public void Parse(byte[] data) {
        StringBuilder incoming = new StringBuilder();
        try {
            String str = new String(data, "UTF-8");
            incoming.append(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int location;
        while ((location = incoming.indexOf(mDelimiter)) >= 0) {
            boolean ok = mPacket.end(incoming.substring(0, location + mDelimiter.length()));
            incoming.delete(0, location + mDelimiter.length());

            // If the packet created was ok, then proceed to send it on.
            if (ok) {
                onPacket(mPacket);
            }
            // Create a new packet, in case user of onPacket does not clone the previous packet.
            mPacket = new StreamPacket();
        }
        if (incoming.length() > 0) {
            mPacket.append(incoming.toString());
        }
    }

    public void onPacket(IPacket packet) {
        // Do nothing, allowing an inheriting class to actually decide what this packet is.
    }

    private StreamPacket mPacket;
    private String mDelimiter;
}

