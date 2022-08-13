package de.backxtar.remoteClientMQTT1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.UUID;

public class ClientPublisher {
    private final MqttClient client;
    private final MqttConnectOptions options;
    private final String topic;
    private final String clientID;
    private final String broker;

    public ClientPublisher(String clientID, String broker, String topic) throws MqttException {
        this.clientID = clientID;
        this.broker = broker;
        this.topic = topic;

        String publisherId = UUID.randomUUID().toString();
        this.client = new MqttClient(broker,publisherId);
        this.options = new MqttConnectOptions();
        this.options.setCleanSession(true);
        this.options.setConnectionTimeout(5);
        this.client.connect(this.options);
    }

    public void send() {
        try {
            MqttMessage msg = values();
            msg.setQos(0);
            msg.setRetained(true);
            this.client.publish(topic, msg);
            System.out.println("Message Published: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttMessage values() {
            final Random rand = new Random();
            int n = rand.nextInt(30);
            byte[] payload = String.format(String.valueOf(n)).getBytes();
            return new MqttMessage(payload);
    }
}
