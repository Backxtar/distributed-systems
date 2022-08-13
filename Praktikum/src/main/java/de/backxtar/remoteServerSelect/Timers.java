package de.backxtar.remoteServerSelect;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class Timers {
    /* Global Variables */
    private final HashMap<InetSocketAddress, Long> timers;

    /**
     * Constructor for timers object.
     * @param sAddresses as array.
     */
    public Timers(InetSocketAddress[] sAddresses) {
        this.timers = new HashMap<>();

        for (InetSocketAddress address : sAddresses) {
            this.timers.put(address, System.currentTimeMillis());
        }
    }

    /**
     * Get current timer bound to the address.
     * @param address of the client.
     * @return timestamp as long.
     */
    public Long getTimer(InetSocketAddress address) {
        return timers.get(address);
    }

    /**
     * Reset the timer bound to the address.
     * @param address of the client.
     */
    public void resetTimer(InetSocketAddress address) {
        timers.replace(address, System.currentTimeMillis());
    }
}
