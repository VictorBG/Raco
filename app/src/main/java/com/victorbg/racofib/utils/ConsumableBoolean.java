package com.victorbg.racofib.utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A special boolean that is consumed.
 * <p>
 * {@link Boolean#TRUE} is converted to {@link Boolean#FALSE}
 * once retrieved, otherwise the value is not modified
 */
public class ConsumableBoolean {

    private AtomicBoolean bool = new AtomicBoolean(false);

    public ConsumableBoolean(Boolean initialState) {
        setValue(initialState);
    }

    public Boolean getValue() {
        return bool.getAndSet(false);
    }

    public ConsumableBoolean setValue(Boolean bool) {
        this.bool.set(bool);
        return this;
    }
}
