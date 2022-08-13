package de.backxtar.RemoteClientMQTTSubscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class ClientSubscribe implements MqttCallback {
    private MqttClient client;
    private MqttConnectOptions options;
    private SensorData data;

    public ClientSubscribe(String clientID, String broker) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        this.client = new MqttClient(broker, clientID, persistence);
        this.options = new MqttConnectOptions();
        this.options.setCleanSession(true);
        this.options.setConnectionTimeout(5);
        this.client.connect(options);
    }

    public void receive(String topic) {
        try {
            this.client.setCallback(this);
            this.client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectionLost(Throwable arg0) {

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {

    }

    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Sensor ID: " + topic);
        System.out.println("Temp: " +message.toString());
        System.out.println("---------------------------");

        this.data = new SensorData(Integer.parseInt(message.toString()), topic);
    }

    public void receivePull() {
        try (DatagramChannel server = DatagramChannel.open()) {
            InetSocketAddress sAddr = new InetSocketAddress(8888);
            server.bind(sAddr);

            ByteBuffer receive = ByteBuffer.allocate(1024);

            while (true) {
                SocketAddress remoteAddr = server.receive(receive);
                receive.flip();

                int limits = receive.limit();
                byte[] bytes = new byte[limits];
                receive.get(bytes, 0, limits);
                String msg = new String(bytes);
                System.out.println(msg);

                if (msg.equalsIgnoreCase("get_data")) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);
                    ByteBuffer send = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
                    server.send(send, remoteAddr);
                }
                receive.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
