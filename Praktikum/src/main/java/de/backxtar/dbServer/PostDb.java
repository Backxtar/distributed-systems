package de.backxtar.dbServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PostDb {
    private Socket socket;

    public PostDb(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
    }

    public void transferDb() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
        final String key = "P=)LVRh3YE|}UYM#pR7sp-rtPiX'Kr";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(DbServer1.data1);

        writer.write("POST HTTP/1.1\r\n");
        writer.write("Auth0: " + key + "\r\n");
        writer.write("Content-Length: " + json.length() + "\r\n");
        writer.write("\r\n");

        writer.write(json, 0, json.length());
        writer.flush();
    }
}
