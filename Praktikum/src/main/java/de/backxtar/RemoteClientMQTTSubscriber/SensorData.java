package de.backxtar.RemoteClientMQTTSubscriber;

public class SensorData {
    /* Global variables */
    private int temp;
    private String manufacturer;

    /**
     * Sensor datas constructor.
     * @param temp as random value.
     * @param manufacturer as custom value.
     */
    public SensorData(int temp, String manufacturer) {
        this.temp = temp;
        this.manufacturer = manufacturer;
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

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
