package de.backxtar.dbServer;

import org.apache.thrift.TException;

public class CalculatorHandler implements Handler.Iface {
    /* Global Variables */
    int identifier = 999999;

    /**
     * Create Entry.
     * @param ip of the sensor.
     * @param port of the sensor.
     * @param manufacturer of the sensor.
     * @param temp sensor wrote.
     * @return boolean of success or failure.
     * @throws TException if something went wrong.
     */
    @Override
    public boolean createEntry(String ip, int port, String manufacturer, int temp) throws TException {
        boolean exist = false;

        for (SensorData sensorData : DbServer1.data1) {
            if (sensorData.getIdentifier() == identifier) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            SensorData sensorData = new SensorData(
                    identifier,
                    ip,
                    port,
                    temp,
                    manufacturer,
                    System.currentTimeMillis(),
                    Flag.AVAILABLE
            );
            identifier--;
            DbServer1.data1.add(sensorData);
            return true;
        } else return false;
    }

    /**
     * Read Entry.
     * @param identifier of the data object.
     * @return the temp as int.
     * @throws TException if something went wrong.
     */
    @Override
    public int readEntry(int identifier) throws TException {
        int temp = -1;

        for (SensorData sensorData : DbServer1.data1)
            if (sensorData.getIdentifier() == identifier) {
                temp = sensorData.getTemp();
                break;
            }

        return temp;
    }

    /**
     * Update Entry.
     * @param identifier of the data to update.
     * @param temp as the param to be updated.
     * @return boolean of success or failure.
     * @throws TException if something went wrong.
     */
    @Override
    public boolean updateEntry(int identifier, int temp) throws TException {
        for (SensorData sensorData : DbServer1.data1) {
            if (sensorData.getIdentifier() == identifier) {
                sensorData.setTemp(temp);
                sensorData.setFlag(Flag.UPDATE);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete Entry / Mark to delete.
     * @param identifier of the object to delete.
     * @return boolean of success or failure.
     * @throws TException if something went wrong.
     */
    @Override
    public boolean deleteEntry(int identifier) throws TException {
        for (SensorData sensorData : DbServer1.data1) {
            if (sensorData.getIdentifier() == identifier) {
                sensorData.setFlag(Flag.DELETE);
                return true;
            }
        }
        return false;
    }
}
