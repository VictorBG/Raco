package com.victorbg.racofib.data.repository.base;

import androidx.annotation.Nullable;
import java.text.ParseException;

public interface Function<T, R> {

  T run(@Nullable R data);
}
