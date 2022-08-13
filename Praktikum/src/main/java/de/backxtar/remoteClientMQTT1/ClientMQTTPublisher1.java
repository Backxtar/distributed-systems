package de.backxtar.remoteClientMQTT1;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMQTTPublisher1 {

    public static void main(String[] args) throws MqttException {
        ClientPublisher clientPublisher = new ClientPublisher("1", "tcp://localhost:1883", "SUZUKI");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(clientPublisher::send, 0, 10, TimeUnit.SECONDS);
    }
}
