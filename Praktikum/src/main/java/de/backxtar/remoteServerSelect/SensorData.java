package de.backxtar.remoteServerSelect;

public class SensorData {
    /* Global variables */
    private String ip;
    private int port;
    private int temp;
    private String manufacturer;
    private long timestamp;

    /**
     * Sensor datas constructor.
     * @param temp as random value.
     * @param manufacturer as custom value.
     */
    public SensorData(String ip, int port, int temp, String manufacturer, long timestamp) {
        this.ip = ip;
        this.port = port;
        this.temp = temp;
        this.manufacturer = manufacturer;
        this.timestamp = timestamp;
    }

    /**
     * Empty Constructor for Gson
     */
    public SensorData() {}

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
}
