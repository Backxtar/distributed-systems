package de.backxtar.remoteClientSelect;

public class SensorData {
    /* Global variables */
    private final int temp;
    private final String manufacturer;

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
}
