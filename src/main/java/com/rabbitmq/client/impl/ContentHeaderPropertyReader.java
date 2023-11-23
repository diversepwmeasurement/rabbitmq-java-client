// Copyright (c) 2007-2023 Broadcom. All Rights Reserved. The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 2.0 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.


package com.rabbitmq.client.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.rabbitmq.client.ContentHeader;
import com.rabbitmq.client.LongString;

/**
 * Parses an AMQP wire-protocol {@link ContentHeader} from a
 * DataInputStream. Methods on this object are usually called from
 * autogenerated code.
 */
public class ContentHeaderPropertyReader {
    /** Stream we are reading from */
    private final ValueReader in;

    /** Current field flag word */
    private int flagWord;

    /** Current flag position counter */
    private int bitCount;

    /**
     * Protected API - Constructs a reader from the given input stream
     */
    public ContentHeaderPropertyReader(DataInputStream in) throws IOException {
        this.in = new ValueReader(in);
        this.flagWord = 1; // just the continuation bit
        this.bitCount = 15; // forces a flagWord read
    }

    private boolean isContinuationBitSet() {
        return (flagWord & 1) != 0;
    }

    public void readFlagWord() throws IOException {
        if (!isContinuationBitSet()) {
            // FIXME: Proper exception class!
            throw new IOException("Attempted to read flag word when none advertised");
        }
        flagWord = in.readShort();
        bitCount = 0;
    }

    public boolean readPresence() throws IOException {
        if (bitCount == 15) {
            readFlagWord();
        }

        int bit = 15 - bitCount;
        bitCount++;
        return (flagWord & (1 << bit)) != 0;
    }

    public void finishPresence() throws IOException {
        if (isContinuationBitSet()) {
            // FIXME: Proper exception class!
            throw new IOException("Unexpected continuation flag word");
        }
    }

    /** Reads and returns an AMQP short string content header field. */
    public String readShortstr() throws IOException {
        return in.readShortstr();
    }

    /** Reads and returns an AMQP "long string" (binary) content header field. */
    public LongString readLongstr() throws IOException {
        return in.readLongstr();
    }

    /** Reads and returns an AMQP short integer content header field. */
    public Integer readShort() throws IOException {
        return in.readShort();
    }

    /** Reads and returns an AMQP integer content header field. */
    public Integer readLong() throws IOException {
        return in.readLong();
    }

    /** Reads and returns an AMQP long integer content header field. */
    public Long readLonglong() throws IOException {
        return in.readLonglong();
    }

    /** Reads and returns an AMQP table content header field. */
    public Map<String, Object> readTable() throws IOException {
        return in.readTable();
    }

    /** Reads and returns an AMQP octet content header field. */
    public int readOctet() throws IOException {
        return in.readOctet();
    }

    /** Reads and returns an AMQP timestamp content header field. */
    public Date readTimestamp() throws IOException {
        return in.readTimestamp();
    }
}
