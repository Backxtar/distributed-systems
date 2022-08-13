package de.backxtar.remoteClientSelect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Random;

public class AsyncUDPClient {
    private final Selector selector;

    /**
     * AsyncUDPClient Constructor.
     * @param port to listen.
     * @throws IOException if something went wrong.
     */
    public AsyncUDPClient(int port) throws IOException {
        /* Global Variables */
        final InetSocketAddress sPort = new InetSocketAddress(port);
        this.selector = Selector.open();
        final DatagramChannel client = DatagramChannel.open();
        client.socket().bind(sPort);
        client.configureBlocking(false);

        final SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_READ);
        final Buffer buffer = new Buffer();
        clientKey.attach(buffer);
    }

    /**
     * Run client method.
     * @throws IOException if something went wrong.
     */
    public void start() throws IOException {
        while (true) {
            this.selector.select();
            final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();

            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid()) continue;

                if (key.isReadable()) read(key);
                if (key.isWritable()) write(key);
            }
        }
    }

    /**
     * Read incoming requests with specific key.
     * @param key as identifier.
     * @throws IOException if something went wrong.
     */
    private void read(SelectionKey key) throws IOException {
        final DatagramChannel client = (DatagramChannel) key.channel();
        final Buffer buffer = (Buffer) key.attachment();
        buffer.setSocketAddr(client.receive(buffer.getReq()));
        buffer.getReq().flip();

        int limits = buffer.getReq().limit();
        byte[] bytes = new byte[limits];
        buffer.getReq().get(bytes, 0, limits);
        final String request = new String(bytes);
        System.out.println(request);
        generateData(request, buffer, key);
    }

    /**
     * Generate data if request code is ok.
     * @param request code as String.
     * @param buffer as location object to write in.
     * @param key as identifier.
     */
    private void generateData(String request, Buffer buffer, SelectionKey key) {
        if (!request.equalsIgnoreCase("get_data")) return;

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final Random rand = new Random();
        int n = rand.nextInt(30);
        SensorData sensorData = new SensorData(n, "AUDI");
        final String json = gson.toJson(sensorData);
        buffer.setResp(ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8)));

        key.interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Write data to datagram with specific key.
     * @param key as identifier.
     * @throws IOException if something went wrong.
     */
    private void write(SelectionKey key) throws IOException {
        final DatagramChannel client = (DatagramChannel) key.channel();
        final Buffer buffer = (Buffer) key.attachment();
        client.send(buffer.getResp(), buffer.getSocketAddr());

        key.interestOps(SelectionKey.OP_READ);
    }
}
