package de.backxtar.dbController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DbControllerSocket {
    /* Global Variables */
    private final ServerSocket serverSocket;
    private ArrayList<SensorData> data;
    private final ScheduledExecutorService executorService;
    private int generatedId;

    /**
     * Constructor of the AsyncDbServer.
     * @param port as int.
     * @throws IOException if something went wrong.
     */
    public DbControllerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.data = new ArrayList<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.generatedId = 0;
    }

    /**
     * Starts the server.
     * @throws IOException if something went wrong.
     */
    public void start() throws IOException {
        this.executorService.scheduleAtFixedRate(this::twoPhaseCommit, 0, 15, TimeUnit.SECONDS);

        while (true) {
            final HttpPost httpPost = new HttpPost();
            final Socket socket = serverSocket.accept();
            socket.setKeepAlive(true);

            read(socket, httpPost);
            write(socket, httpPost);

            System.out.println("Database size: " + this.data.size());
            //printDb();
        }
    }

    /**
     * Collect data from an inputStream.
     * @param socket from ServerSocket
     * @param httpPost as object.
     * @throws IOException if something went wrong.
     */
    private void read(Socket socket, HttpPost httpPost) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder header = new StringBuilder();

        final String[] values = {"Auth0: ", "Content-Length: ", ""};
        String line;

        while ((line = reader.readLine()) != null) {
            header.append(line).append("\r\n");
            if (line.startsWith(values[0])) httpPost.setAuth0(line.substring(values[0].length()));
            if (line.startsWith(values[1]))
                httpPost.setContent_length(Integer.parseInt(line.substring(values[1].length())));
            if (line.equalsIgnoreCase(values[2])) break;
        }
        char[] buf = new char[httpPost.getContent_length()];
        reader.read(buf, 0, httpPost.getContent_length());

        httpPost.setHeader(header.toString());
        httpPost.setBody(String.valueOf(buf));
    }

    /**
     * Convert data to in memory database if auth0 key is correct. Respond to sender.
     * @param socket from ServerSocket
     * @param httpPost as object.
     * @throws IOException if something went wrong.
     */
    private void write(Socket socket, HttpPost httpPost) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8));
        final String auth0 = "P=)LVRh3YE|}UYM#pR7sp-rtPiX'Kr";
        final String httpRes;

        writer.write("POST HTTP/1.1\r\n");

        if (httpPost.getAuth0() != null && auth0.equals(httpPost.getAuth0())) {
            ArrayList<SensorData> pulledData = mapData(httpPost);
            this.data.addAll(pulledData);

            httpRes = "HTTP/1.1 200 OK\r\n";
            System.out.println("Send response: HTTP 200 OK\n");
        } else {
            httpRes = "HTTP/1.1 500 ERROR\r\n";
            System.out.println("Send response: HTTP 500 FAIL\n");
        }
        writer.write("Content-Length: " + httpRes.length() + "\r\n");
        writer.write("\r\n");
        writer.write(httpRes, 0 , httpRes.length());
        writer.flush();
    }

    /**
     * Convert json to object array via GSON.
     * @param httpPost as object to read body from.
     * @return ArrayList<SensorData>
     */
    private ArrayList<SensorData> getData(HttpPost httpPost) {
        final Gson gson = new Gson();
        final Type return_type = new TypeToken<ArrayList<SensorData>>() {}.getType();
        return gson.fromJson(httpPost.getBody(), return_type);
    }

    /**
     * Generate identifier.
     * @param httpPost as param to get data from.
     * @return new Arraylist of SensorData.
     */
    private ArrayList<SensorData> mapData(HttpPost httpPost) {
        ArrayList<SensorData> pulledData = getData(httpPost);
        pulledData.forEach(sensor -> sensor.setIdentifier(generateIdentifier()));
        return pulledData;
    }

    /**
     * Generate id.
     * @return new id as int.
     */
    private int generateIdentifier() {
        return ++generatedId;
    }

    /**
     * Prints the in memory database.
     */
    private void printDb() {
        if (this.data.isEmpty()) return;

        System.out.println("In Memory Database:");
        System.out.println("----------------------------");

        this.data.forEach(sensor -> {
            final long dateTime = sensor.getTimestamp();
            final Date date = new Date(dateTime);
            final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            final String output = "Sensor: " + sensor.getIp() + " | " + sensor.getPort() + "\r\n"
                    + "Manufacturer: " + sensor.getManufacturer() + "\r\n"
                    + "Temp: " + sensor.getTemp() + "\r\n"
                    + "Timestamp: " + df.format(date) + "\r\n"
                    + "----------------------------";
            System.out.println(output);
        });
        System.out.println("Database size: " + this.data.size() + "\r\n");
    }

    /**
     * Two phase commit.
     */
    private void twoPhaseCommit() {
        if (this.data.isEmpty()) return;
        int sizeBefore = this.data.size();

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(this.data);

        boolean server1 = commit("127.0.0.1", 1111, json);
        boolean server2 = false;//commit("127.0.0.1", 2222, json);

        int sizeAfter = this.data.size();
        if (server1 || server2) {
            if (sizeBefore == sizeAfter) this.data = new ArrayList<>();
            else clearStorage(sizeBefore);
        }
    }

    /**
     * Commit via httpPost to db.
     * @param ip of the db server.
     * @param port of the db server.
     * @param json to push.
     * @return success or failure as boolean.
     */
    private boolean commit(String ip, int port, String json) {
        try {
            HttpPostRequest httpPostRequest = new HttpPostRequest(ip, port);
            httpPostRequest.sendRequest(json);
            return true;
        } catch (IOException io) {
            System.out.println("Database (" + ip + " | " + port + ") is offline!");
            return false;
        }
    }

    /**
     * Clears the storage after successful push.
     * @param amount length to delete.
     */
    private void clearStorage(int amount) {
        for (int i = 0; i < amount; i++)
            this.data.remove(i);
    }
}
