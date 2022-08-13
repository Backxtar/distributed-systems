package de.backxtar.dbController;

import java.io.IOException;

public class DbController {
    public static void main(String[] args) throws IOException {
        DbControllerSocket dbControllerSocket = new DbControllerSocket(6969);
        dbControllerSocket.start();
    }
}
