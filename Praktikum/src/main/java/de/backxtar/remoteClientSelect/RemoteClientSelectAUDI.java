package de.backxtar.remoteClientSelect;

import java.io.IOException;

public class RemoteClientSelectAUDI {
    public static void main(String[] args) throws IOException {
        AsyncUDPClient asyncUDPClient = new AsyncUDPClient(6666);
        asyncUDPClient.start();
    }
}
