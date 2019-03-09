package com.victorbg.racofib.data.repository.network;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class RateLimiterTest {

    @Test
    public void shouldFetch() throws InterruptedException {

        RateLimiter limiter = new RateLimiter(1, TimeUnit.MILLISECONDS);

        assertTrue(limiter.shouldFetch());
        Thread.sleep(2);
        assertTrue(limiter.shouldFetch());

        limiter = new RateLimiter(1, TimeUnit.HOURS);

        assertTrue(limiter.shouldFetch());
        assertFalse(limiter.shouldFetch());
    }

    @Test
    public void reset() {

        RateLimiter limiter = new RateLimiter(1, TimeUnit.HOURS);

        assertTrue(limiter.shouldFetch());
        assertFalse(limiter.shouldFetch());

        limiter.reset();

        assertTrue(limiter.shouldFetch());
    }
}