package de.backxtar.dbServer;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class RPCServer {
    private int port;
    private CalculatorHandler handler;
    private Handler.Processor processor;
    private TServer server;

    public RPCServer(int port) {
       this.port = port;

       this.handler = new CalculatorHandler();
       this.processor = new Handler.Processor(handler);
    }

    public void start() {
        try (TServerTransport serverTransport = new TServerSocket(port)) {
            this.server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("RPC server started!");
            this.server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
