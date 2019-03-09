package com.victorbg.racofib.data.repository.base;

import javax.annotation.Nullable;

public class Resource<T> {

    public final T data;
    public final Status status;
    public final String message;

    public Resource(Status status, @Nullable T data, @Nullable String message) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }
}
