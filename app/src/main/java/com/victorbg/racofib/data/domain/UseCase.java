package com.victorbg.racofib.data.domain;

import com.victorbg.racofib.data.repository.AppExecutors;

public abstract class UseCase<P, R> {

    protected final AppExecutors appExecutors;

    public UseCase(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    /**
     * Executes the use case without parameters
     *
     * @return R
     */

    public abstract R execute();

    /**
     * Executes the use case with parameters
     *
     * @param parameter P
     * @return R
     */
    public abstract R execute(P parameter);
}
