package de.backxtar.dbController;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpPostRequest {
    /* Global Variables */
    private Socket socket;
    private final String ip;
    private final int port;

    /**
     * HttpPostRequest constructor.
     * @throws IOException if something went wrong.
     */
    public HttpPostRequest(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.socket.setSoTimeout(2500);
        this.socket.setReuseAddress(true);
        this.ip = ip;
        this.port = port;
    }

    /**
     * SendRequest to Db Server.
     * @param jsonData as String.
     * @throws IOException if something went wrong.
     */
    public void sendRequest(String jsonData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));

        writer.write("ping", 0, 4);
        writer.flush();
        System.out.println("Request: Ping DB-Server (" + ip + " | " + port + ")");

        char[] buf = new char[4];
        reader.read(buf, 0, 4);
        final String response = String.valueOf(buf);
        System.out.println("Response DB-Server (" + ip + " | " + port + "): " + response);

        if (!response.equalsIgnoreCase("pong")) throw new IOException("No pong!");

        write(writer, jsonData);
        int responseCode = read(reader);
        closeConnection(writer, reader);

        if (responseCode != 200) throw new IOException("Wrong parameters!");
    }

    /**
     * Write data to server.
     * @param writer of output stream.
     * @param jsonData to send.
     * @throws IOException if something went wrong.
     */
    private void write(final BufferedWriter writer, final String jsonData) throws IOException {
        final String key = "P=)LVRh3YE|}UYM#pR7sp-rtPiX'Kr";

        writer.write("POST HTTP/1.1\r\n");
        writer.write("Auth0: " + key + "\r\n");
        writer.write("Function: " + "insert" + "\r\n");
        writer.write("Content-Length: " + jsonData.length() + "\r\n");
        writer.write("\r\n");

        writer.write(jsonData, 0, jsonData.length());
        writer.flush();
        System.out.println("Transfer: JSON");
    }

    /**
     * Read response from server.
     * @param reader of input stream.
     * @return response code as int.
     * @throws IOException if something went wrong.
     */
    private int read(final BufferedReader reader) throws IOException {
        final String[] values = {"Content-Length: ", ""};
        int responseLength = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith(values[0])) responseLength = Integer.parseInt(line.substring(values[0].length()));
            if (line.equalsIgnoreCase(values[1])) break;
        }
        char[] buf = new char[responseLength];
        reader.read(buf, 0, responseLength);
        String[] args = String.valueOf(buf).split(" ");

        int responseCode;

        try { responseCode = Integer.parseInt(args[1]); }
        catch (NumberFormatException ex) { responseCode = 0; }

        System.out.println("Response: " + String.valueOf(buf));
        return responseCode;
    }

    /**
     * Close the socket connection to server.
     * @param writer to close.
     * @param reader to close.
     * @throws IOException if something went wrong.
     */
    private void closeConnection(BufferedWriter writer, BufferedReader reader) throws IOException {
        writer.close();
        reader.close();
        this.socket.close();
    }
}
