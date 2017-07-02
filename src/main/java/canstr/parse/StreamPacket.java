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

public class StreamPacket implements IPacket {

    public StreamPacket(int maxSize) {
        mDataBuilder = new StringBuilder();
        mMaxSize = maxSize;
        overrun = false;
    }

    public StreamPacket() {
        this(2048);
    }

    /**
     * Append the string to the packet, discarding it completely and flagging if maxSize is overrun.
     * Use end() for the last addition of data
     * @param str
     */
    void append(String str) {
        if ((mDataBuilder.length() + str.length()) > mMaxSize) {
            mDataBuilder.delete(0, mDataBuilder.length() - 1);
            overrun = true;
        }
        mDataBuilder.append(str);
    }

    /**
     * Call this when the string to be added will be the end of the packet.
     * @param str
     * @return true if successful packet created, false if there was a problem like buffer overrun.
     */
    boolean end(String str) {
        boolean packetSuccess = !overrun;
        if (packetSuccess) {
            append(str);
        }
        overrun = false; // reset overrun so we start looking for next packet.
        return packetSuccess;
    }

    public String getString() {
        return mDataBuilder.toString();
    }

    /**
     * Use string builder to do the appending
     */
    private StringBuilder mDataBuilder;
    private int mMaxSize;
    private boolean overrun;
}
