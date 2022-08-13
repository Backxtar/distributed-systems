package de.backxtar.remoteClientSelect2;

import java.io.IOException;

public class RemoteClientSelectBMW {
    public static void main(String[] args) throws IOException {
        AsyncUDPClient asyncUDPClient = new AsyncUDPClient(7777);
        asyncUDPClient.start();
    }
}
