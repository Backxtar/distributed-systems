package de.backxtar.dbServer2;

import de.backxtar.dbServer.Flag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DbServer2 {
    public static ArrayList<SensorData> data2 = new ArrayList<>();

    public static void main(String[] args) {
        ScheduledExecutorService executorService1 = Executors.newSingleThreadScheduledExecutor();
        executorService1.scheduleAtFixedRate(DbServer2::dumpDatabase, 0, 45, TimeUnit.SECONDS);
        ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        executorService2.scheduleAtFixedRate(DbServer2::send, 0, 15, TimeUnit.SECONDS);
        ScheduledExecutorService executorService3 = Executors.newSingleThreadScheduledExecutor();
        executorService3.scheduleAtFixedRate(DbServer2::delete, 0, 30, TimeUnit.SECONDS);

        DbControllerConn controllerConn = new DbControllerConn(2222);
        Thread controllerConnection = new Thread(controllerConn::start);
        controllerConnection.start();

        ReceiveDb receiveDb = new ReceiveDb(1313);
        Thread receiveDatabase = new Thread(receiveDb::accept);
        receiveDatabase.start();
    }

    private static void send() {
        if (data2.isEmpty()) return;
        try {
            PostDb postDb = new PostDb("127.0.0.1",  3131);
            postDb.transferDb();
        } catch (IOException ignored) {}
    }

    private static void dumpDatabase() {
        System.out.println("Database size: " + data2.size());
        if (data2.isEmpty()) return;

        Collections.sort(data2);

        File dumped = new File("database2.txt");
        boolean written = false;

        try {
            if (!dumped.createNewFile()) written = dumped.delete();

            FileWriter writer = new FileWriter(dumped.getName());
            writer.write("Sensor Database:\r\n");
            writer.write("----------------------------\r\n");

            for (SensorData sensor : data2) {
                if (sensor.getFlag() == Flag.DELETE) continue;
                final long dateTime = sensor.getTimestamp();
                final Date date = new Date(dateTime);
                final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                final String output = "Identifier: " + sensor.getIdentifier() + "\r\n"
                        + "Sensor: " + sensor.getIp() + " | " + sensor.getPort() + "\r\n"
                        + "Manufacturer: " + sensor.getManufacturer() + "\r\n"
                        + "Temp: " + sensor.getTemp() + "\r\n"
                        + "Timestamp: " + df.format(date) + "\r\n"
                        //+ "Timestamp: " + sensor.getTimestamp() + "\r\n"
                        + "----------------------------\r\n";
                writer.write(output);
            }
            writer.close();
            if (written) System.out.println("Database dumped!");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void delete() {
        for (int i = 0; i < DbServer2.data2.size(); i++) {
            if (DbServer2.data2.get(i).getFlag() == Flag.DELETE) {
                DbServer2.data2.remove(i);
                i--;
            }
        }
    }
}
