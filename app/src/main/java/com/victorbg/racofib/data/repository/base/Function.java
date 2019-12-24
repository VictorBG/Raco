package com.victorbg.racofib.data.repository.base;

import androidx.annotation.Nullable;

public interface Function<T, R> {

  T run(@Nullable R data);
}
