package com.victorbg.racofib.data.repository.base.functions;

import androidx.annotation.Nullable;
import java.text.ParseException;

@FunctionalInterface
public interface Function<T, R> {

  T run(@Nullable R data);
}
