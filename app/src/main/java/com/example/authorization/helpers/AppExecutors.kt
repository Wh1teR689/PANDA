package com.example.authorization.helpers

import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class AppExecutors(
    private val diskIO: Executor
) {
    constructor(): this(
        Executors.newSingleThreadExecutor()
    )

    fun diskIO(): Executor {
        return diskIO
    }
}