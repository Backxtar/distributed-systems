package de.backxtar.remoteServerSelect;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class Buffer {
    /* Global Variables */
    private ByteBuffer buffer;

    /**
     * Buffer Constructor.
     */
    public Buffer() {
        this.buffer = ByteBuffer.allocate(1024);
    }

    /**
     * Get the buffer from the instance.
     * @return the instances buffer.
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }
}
