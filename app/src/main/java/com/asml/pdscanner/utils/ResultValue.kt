package com.asml.pdscanner.utils

sealed class ResultValue<out T> {
    data class Success<out T>(val value: T) : ResultValue<T>()
    data class Failure(val exception: Throwable) : ResultValue<Nothing>()
}