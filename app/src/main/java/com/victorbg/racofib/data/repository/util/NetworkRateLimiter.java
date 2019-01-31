package com.victorbg.racofib.data.repository.util;

import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

public class NetworkRateLimiter {

    private long timestamp = -1;
    private final long timeout;

    public NetworkRateLimiter(int timeout, TimeUnit timeUnit) {
        this.timeout = timeUnit.toMillis(timeout);
    }

    public synchronized boolean shouldFetch() {
        long now = now();
        if (timeout == -1) {
            timestamp = now;
            return true;
        }

        if (now - timestamp > timestamp) {
            timestamp = now;
            return true;
        }

        return false;
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    public void reset() {
        timestamp = -1;
    }
}
