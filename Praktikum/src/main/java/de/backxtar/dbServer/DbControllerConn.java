package de.backxtar.dbServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DbControllerConn {
    /* Global Variables */
    private ServerSocket serverSocket;

    /**
     * Constructor of DbControllerConn
     * @param port to listen to.
     */
    public DbControllerConn(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Starts the listening process.
     */
    public void start() {
        try {
            while (this.serverSocket != null) {
                HttpPost httpPost = new HttpPost();
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

                sendPong(reader, writer, httpPost);
                read(reader, httpPost);
                write(writer, httpPost);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Send Pong to dbController
     * @param reader to read from.
     * @param writer to write threw.
     * @param httpPost data to send.
     * @throws IOException if something went wrong.
     */
    private void sendPong(BufferedReader reader, BufferedWriter writer, HttpPost httpPost) throws IOException {
        char[] buf = new char[4];
        reader.read(buf, 0, 4);
        System.out.println(String.valueOf(buf));
        httpPost.setPing(String.valueOf(buf));

        if (!httpPost.getPing().equalsIgnoreCase("ping")) return;

        writer.write("pong");
        writer.flush();
        System.out.println("Wrote Pong");
    }

    /**
     * Read pushed data from controller.
     * @param reader to read from.
     * @param httpPost to save in.
     * @throws IOException if something went wrong.
     */
    private void read(BufferedReader reader, HttpPost httpPost) throws IOException {
        if (!httpPost.getPing().equalsIgnoreCase("ping")) return;

        StringBuilder header = new StringBuilder();
        final String[] values = {"Auth0: ", "Function: ", "Param: ", "Content-Length: ", ""};
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);

            header.append(line).append("\r\n");
            if (line.startsWith(values[0])) httpPost.setAuth0(line.substring(values[0].length()));
            if (line.startsWith(values[1])) httpPost.setFunction(line.substring(10));
            if (line.startsWith(values[2])) httpPost.setFunctionParam(line.substring(7));
            if (line.startsWith(values[3]))
                httpPost.setContent_length(Integer.parseInt(line.substring(values[3].length())));
            if (line.equalsIgnoreCase(values[4])) break;
        }
        char[] buf = new char[httpPost.getContent_length()];
        reader.read(buf, 0, httpPost.getContent_length());
        System.out.println(String.valueOf(buf));

        httpPost.setHeader(header.toString());
        httpPost.setBody(String.valueOf(buf));
    }

    /**
     * Write httpResponse to dbController.
     * @param writer to write threw.
     * @param httpPost to check parameters.
     * @throws IOException if something went wrong.
     */
    private void write(BufferedWriter writer, HttpPost httpPost) throws IOException {
        final String auth0 = "P=)LVRh3YE|}UYM#pR7sp-rtPiX'Kr";
        String httpRes;

        writer.write("POST HTTP/1.1\r\n");

        if (httpPost.getAuth0() != null && auth0.equals(httpPost.getAuth0())) {
            final ArrayList<SensorData> received = getData(httpPost);
            DbServer1.data1.addAll(received);
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
        ArrayList<SensorData> data = gson.fromJson(httpPost.getBody(), return_type);
        data.forEach(sensor -> sensor.setFlag(Flag.AVAILABLE));
        return data;
    }
}
