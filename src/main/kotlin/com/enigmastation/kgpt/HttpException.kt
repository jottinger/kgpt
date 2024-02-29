package com.enigmastation.kgpt

/**
 * This is a generalized HTTP Exception class for `kgpt`.
 *
 * @param code the HTTP status code of the error condition
 * @param message the message, if any, that accompanies the error
 * @param body any body text that accompanies the error
 */
class HttpException(
    code: Int,
    message: String,
    body: String? = null
) : Throwable("$code $message ${body?:""}")
