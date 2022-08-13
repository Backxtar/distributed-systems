package de.backxtar.RemoteClientMQTTSubscriber;

import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.IOException;

public class ClientMQTTSubscriber {

    public static void main(String[] args) throws MqttException, IOException {
        ClientSubscribe client = new ClientSubscribe("69","tcp://localhost:1883");
        client.receive("SUZUKI");
        client.receivePull();
    }
}
