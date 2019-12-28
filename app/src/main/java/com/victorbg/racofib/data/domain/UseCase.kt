package com.victorbg.racofib.data.domain

import com.victorbg.racofib.data.repository.AppExecutors
import io.reactivex.disposables.CompositeDisposable

abstract class UseCase<P, R>(protected val appExecutors: AppExecutors) {
    protected var disposables = CompositeDisposable()
    /**
     * Executes the use case without parameters
     *
     * @return R
     */
    abstract fun execute(): R

    /**
     * Executes the use case with parameters
     *
     * @param parameter P
     * @return R
     */
    abstract fun execute(parameter: P): R

}