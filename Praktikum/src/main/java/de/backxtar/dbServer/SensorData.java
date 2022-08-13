package de.backxtar.dbServer;

import org.jetbrains.annotations.NotNull;

public class SensorData implements Comparable<SensorData> {
    /* Global variables */
    private int identifier;
    private String ip;
    private int port;
    private int temp;
    private String manufacturer;
    private long timestamp;
    private Flag flag;

    /**
     * Sensor datas constructor.
     * @param temp as random value.
     * @param manufacturer as custom value.
     */
    public SensorData(int identifier, String ip, int port,
                      int temp, String manufacturer, long timestamp, Flag flag) {
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
        this.temp = temp;
        this.manufacturer = manufacturer;
        this.timestamp = timestamp;
        this.flag = flag;
    }

    /**
     * Empty Constructor for Gson
     */
    public SensorData() {}

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }

    /**
     * Get sensors ip.
     * @return ip as String.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the ip of the sensor.
     * @param ip as String.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get sensors port.
     * @return port as int.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port of the sensor.
     * @param port as int.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get sensors temp.
     * @return temp as int.
     */
    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    /**
     * Get sensors manufacturer.
     * @return manufacturer as String.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Get the sensor data's timestamp.
     * @return timestamp as String.
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Set the sensor data's timestamp.
     * @param timestamp as String.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {
        return flag;
    }

    @Override
    public int compareTo(@NotNull SensorData o) {
        long compare = o.getTimestamp();
        long result = this.timestamp - compare;
        return (int) result;
    }
}
