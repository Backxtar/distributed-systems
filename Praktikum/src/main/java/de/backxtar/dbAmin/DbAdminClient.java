package de.backxtar.dbAmin;

import de.backxtar.dbServer.Handler;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbAdminClient {
    public static void main(String[] args) {
        try {
            TTransport transport1 = new TSocket("127.0.0.1", 9090);
            transport1.open();

            TProtocol protocol1 = new TBinaryProtocol(transport1);
            Handler.Client client1 = new Handler.Client(protocol1);

            perform(client1);
            transport1.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void perform(Handler.Client client) throws TException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean run = true;

        while(run){
            System.out.println("[C] - Create Entry");
            System.out.println("[R] - Read Entry");
            System.out.println("[U] - Update Entry");
            System.out.println("[D] - Delete Entry");

            try {
                String method = reader.readLine();
                switch (method.toLowerCase()) {
                    case "c" -> {
                        final Object[] args = create(reader);
                        final boolean entryCreated = client.createEntry(args[0].toString(), 9090,
                                args[1].toString(), Integer.parseInt(args[2].toString()));
                        if (entryCreated) System.out.println("Entry successful created!");
                        else System.out.println("Entry creation failed!");
                    }
                    case "r" -> {
                        System.out.println("Enter the UID you want to read:");
                        final int identifier = Integer.parseInt(reader.readLine());
                        final int entryRead = client.readEntry(identifier);
                        if (entryRead != -1) System.out.println("Temp of sensor(" + identifier + "): " + entryRead);
                        else System.out.println("Entry read failed!");
                    }
                    case "u" -> {
                        final int[] args = update(reader);
                        final boolean entryUpdate = client.updateEntry(args[0], args[1]);
                        if (entryUpdate) System.out.println("Entry update successful!");
                        else System.out.println("Entry update failed!");
                    }
                    case "d" -> {
                        System.out.println("Enter the UID you want to delete:");
                        final int identifier = Integer.parseInt(reader.readLine());
                        final boolean entryDelete = client.deleteEntry(identifier);
                        if (entryDelete) System.out.println("Entry successful marked to delete!");
                        else System.out.println("Entry delete failed!");
                    }
                    case "e" -> run = false;
                    default -> System.out.println("No such method. Try again!");
                }
            } catch (IOException io) {
                System.out.println("Something went wrong. Try again!");
            }
            System.out.println("------------------------\n\r");
        }
    }

    private static Object[] create(BufferedReader reader) throws IOException {
        Object[] args = new Object[3];
        System.out.println("Enter the sensor IP:");
        args[0] = reader.readLine();
        System.out.println("Enter the manufacturer:");
        args[1] = reader.readLine();
        System.out.println("Enter the temperature:");
        args[2] = Integer.parseInt(reader.readLine());
        return args;
    }

    private static int[] update(BufferedReader reader) throws IOException {
        int[] args = new int[2];
        System.out.println("Enter the UID you want to update:");
        args[0] = Integer.parseInt(reader.readLine());
        System.out.println("Enter the new temp you want to update:");
        args[1] = Integer.parseInt(reader.readLine());
        return args;
    }
}
