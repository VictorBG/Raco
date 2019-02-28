package com.victorbg.racofib.data.repository.network;

import java.util.concurrent.TimeUnit;

public class NetworkRateLimiter {

    private long timestamp = -1;
    private final long timeout;

    public NetworkRateLimiter(int timeout, TimeUnit timeUnit) {
        this.timeout = timeUnit.toMillis(timeout);
    }

    /**
     * Returns if the network call should be fetch based on the
     * timeout and the last time a call has been made. It also
     * stores the current timestamp as the last call timestamp
     * to future checks.
     *
     * @return
     */
    public synchronized boolean shouldFetch() {
        long now = now();
        if (timestamp == -1) {
            timestamp = now;
            return true;
        }

        if (now - timeout > timestamp) {
            timestamp = now;
            return true;
        }

        return false;
    }

    private long now() {
        return System.currentTimeMillis();
    }

    public void reset() {
        timestamp = -1;
    }
}
