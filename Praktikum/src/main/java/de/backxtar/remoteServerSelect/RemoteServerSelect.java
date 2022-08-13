package de.backxtar.remoteServerSelect;

import java.io.IOException;

public class RemoteServerSelect {
    public static void main(String[] args) throws IOException {
        String[] ips = { "127.0.0.1", "127.0.0.1", "127.0.0.1" };
        int[] ports = { 6666, 7777, 8888 };

        AsyncUDPServer asyncUDPServer = new AsyncUDPServer(ips, ports);
        asyncUDPServer.start();
    }
}
