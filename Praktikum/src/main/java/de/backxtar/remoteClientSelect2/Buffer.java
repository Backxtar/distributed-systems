package de.backxtar.remoteClientSelect2;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class Buffer {
    /* Global Variables */
    private ByteBuffer req;
    private ByteBuffer resp;
    private SocketAddress socketAddr;

    /**
     * Constructor of the custom buffer.
     */
    public Buffer() {
        req = ByteBuffer.allocate(1024);
    }

    /**
     * Getter for the request buffer.
     * @return the request buffer.
     */
    public ByteBuffer getReq() {
        return req;
    }

    /**
     * Getter for the response buffer.
     * @return the response buffer.
     */
    public ByteBuffer getResp() {
        return resp;
    }

    /**
     * Set the new response buffer to the current instance.
     * @param resp buffer as object.
     */
    public void setResp(ByteBuffer resp) {
        this.resp = resp;
    }

    /**
     * Get servers socket address.
     * @return servers address as socket object.
     */
    public SocketAddress getSocketAddr() {
        return socketAddr;
    }

    /**
     * Sets the servers socket address to this instance.
     * @param socketAddr as a new object.
     */
    public void setSocketAddr(SocketAddress socketAddr) {
        this.socketAddr = socketAddr;
    }
}
