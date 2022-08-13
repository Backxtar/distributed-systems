package de.backxtar.remoteServerSelect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsyncUDPServer {
    /* Global Variables */
    private final Logger logger = LoggerFactory.getLogger(AsyncUDPServer.class);
    private final int INTERVAL_MS = 10000;
    private final InetSocketAddress[] sAddresses;
    private final Selector selector;
    private final DatagramChannel server;
    private final Timers timers;
    private ArrayList<SensorData> data;

    /**
     * AsyncUDPServer Constructor.
     * @param ips as String array.
     * @param ports as Integer array.
     * @throws IOException if something went wrong.
     */
    public AsyncUDPServer(String[] ips, int[] ports) throws IOException {
        this.sAddresses = new InetSocketAddress[ips.length];

        for (int i = 0; i < ports.length; i++)
            this.sAddresses[i] = new InetSocketAddress(ips[i], ports[i]);

        this.timers = new Timers(sAddresses);
        this.selector = Selector.open();
        this.server = DatagramChannel.open();
        this.server.socket().bind(null);
        this.server.configureBlocking(false);

        SelectionKey serverKey = this.server.register(this.selector, SelectionKey.OP_READ);
        Buffer buffer = new Buffer();
        serverKey.attach(buffer);
        this.data = new ArrayList<>();
    }

    /**
     * Run server method.
     * @throws IOException if something went wrong.
     */
    public void start() throws IOException {
        final ScheduledExecutorService executorService1 = Executors.newSingleThreadScheduledExecutor();
        executorService1.scheduleAtFixedRate(this::request, 0, INTERVAL_MS, TimeUnit.MILLISECONDS);
        final ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        executorService2.scheduleAtFixedRate(this::httpPostDB, 0, 15, TimeUnit.SECONDS);

        while (true) {
            this.selector.select();
            final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();

            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid()) continue;

                if (key.isReadable()) read(key);
            }
        }
    }

    /**
     * Send requests to all clients.
     */
    private void request() {
        Arrays.stream(sAddresses).forEach(address -> {
            try {
                checkTimeout(address);
                this.server.send(ByteBuffer.wrap("get_data".getBytes(StandardCharsets.UTF_8)), address);
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
        System.out.println("Database size: " + this.data.size());
    }

    /**
     * Check for connection timeout.
     * @param address of the client.
     */
    private void checkTimeout(InetSocketAddress address) {
        if (System.currentTimeMillis() - this.timers.getTimer(address) < this.INTERVAL_MS + 1000) return;
        this.logger.warn("Client ("
                + address.getAddress().getHostAddress()
                + " | " + address.getPort()
                + ") is offline!");
        saveData(false, null, address);
        this.timers.resetTimer(address);
    }

    /**
     * Read data from datagram-channel with specific key.
     * @param key as identifier.
     * @throws IOException if something went wrong.
     */
    private void read(SelectionKey key) throws IOException {
        DatagramChannel server = (DatagramChannel) key.channel();
        Buffer data = (Buffer) key.attachment();
        final InetSocketAddress addr = (InetSocketAddress) server.receive(data.getBuffer());
        data.getBuffer().flip();

        int limits = data.getBuffer().limit();
        byte[] bytes = new byte[limits];
        data.getBuffer().get(bytes, 0, limits);
        final String json = new String(bytes);

        saveData(true, json, addr);
        System.out.println(json);
        data.getBuffer().clear();
        this.timers.resetTimer(addr);
    }

    /**
     * Save sensor data to the storage.
     * @param success as boolean.
     * @param json as string. Can be null!
     * @param cAddr as InetSocketAddress.
     */
    private void saveData(boolean success, String json, InetSocketAddress cAddr) {
        final SensorData sensorData;

        if (success && json != null) {
            Gson gson = new Gson();
            sensorData = gson.fromJson(json, SensorData.class);
            sensorData.setIp(cAddr.getAddress().getHostAddress());
            sensorData.setPort(cAddr.getPort());
            sensorData.setTimestamp(System.currentTimeMillis());
        } else {
            sensorData = new SensorData(
                    cAddr.getAddress().getHostAddress(),
                    cAddr.getPort(),
                    -403,
                    "FAILED!",
                    System.currentTimeMillis());
        }
        this.data.add(sensorData);
    }

    /**
     * Posting the data via http POST to the db.
     */
    private void httpPostDB() {
        if (this.data.isEmpty() || this.data.size() < 10) {
            this.logger.info("No data or not enough data available!");
            return;
        }
        System.out.println("Sending data to DB-Controller!");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this.data);

        try {
            HttpPostRequest httpPostRequest = new HttpPostRequest("127.0.0.1", 6969);
            httpPostRequest.makeHttpPost(json);
            this.data = new ArrayList<>();
        } catch (UnknownHostException uhe) {
            this.logger.error("DB-Server unknown!");
        } catch (IOException io) {
            this.logger.error("Error while posting data to db!");
        }
    }
}
