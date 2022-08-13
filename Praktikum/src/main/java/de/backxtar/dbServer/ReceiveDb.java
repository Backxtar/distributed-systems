package de.backxtar.dbServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReceiveDb {
    private ServerSocket serverSocket;

    public ReceiveDb(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void accept() {
        try {
            while (this.serverSocket != null) {
                HttpPost httpPost = new HttpPost();
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(3000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

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

                sync(httpPost);
            }
        } catch (IOException io) {
            System.out.println("Read timeout");
        }
    }

    private void sync(HttpPost httpPost) {
        final ArrayList<SensorData> collected = getData(httpPost);
        boolean synced = false;

        for (SensorData sensor : collected) {
            boolean exists = false, setFlag = false;
            final Flag flag = sensor.getFlag();
            int position = -1;

            for (SensorData toCheck : DbServer1.data1) {
                if (exists(sensor, toCheck)) {
                    if (toCheck.getFlag() != flag) {
                        setFlag = true;
                        position = DbServer1.data1.indexOf(toCheck);
                    }
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                DbServer1.data1.add(sensor);
                synced = true;
            } else {
                if (setFlag && position != -1) {
                    DbServer1.data1.get(position).setFlag(flag);
                    synced = true;
                }
            }
        }
        if (synced) System.out.println("Database synced.");
    }

    private boolean exists(SensorData collected, SensorData toCheck) {
        return  collected.getIdentifier() == toCheck.getIdentifier() &&
                collected.getIp().equalsIgnoreCase(toCheck.getIp()) &&
                collected.getPort() == toCheck.getPort() &&
                collected.getTimestamp() == toCheck.getTimestamp() &&
                collected.getManufacturer().equalsIgnoreCase(toCheck.getManufacturer()) &&
                collected.getTemp() == toCheck.getTemp();
    }

    private ArrayList<SensorData> getData(HttpPost httpPost) {
        final Gson gson = new Gson();
        final Type return_type = new TypeToken<ArrayList<SensorData>>() {}.getType();
        return gson.fromJson(httpPost.getBody(), return_type);
    }
}
