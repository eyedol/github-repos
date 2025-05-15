package com.addhen.livefront.data.model

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val error: DataError) : DataResult<Nothing>
}

sealed interface DataError {
    object NotFound : DataError
    object Network : DataError
    data class Unknown(val message: String) : DataError
}
