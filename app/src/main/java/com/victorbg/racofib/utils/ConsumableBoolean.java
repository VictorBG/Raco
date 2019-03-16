package com.victorbg.racofib.utils;

/**
 * A special boolean that is consumed once retrieved.
 * {@link Boolean#TRUE} is converted to {@link Boolean#FALSE}
 * once retrieved, otherwise the value is not modified
 */
public class ConsumableBoolean {

    private Boolean bool = Boolean.FALSE;

    public ConsumableBoolean(Boolean initialState) {
        setValue(initialState);
    }

    public Boolean getValue() {
        Boolean copy = bool;
        if (bool == Boolean.TRUE) {
            bool = Boolean.FALSE;
        }
        return copy;
    }

    public ConsumableBoolean setValue(Boolean bool) {
        this.bool = bool;
        return this;
    }
}
