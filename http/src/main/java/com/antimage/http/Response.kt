package com.antimage.http

class Response<T>() {

    var code: String? = null

    var msg: String? = null

    var data: T? = null

    constructor(code: String): this() {
        this.code = code
    }

    constructor(code: String, msg: String): this() {
        this.code = code
        this.msg = msg
    }

    fun isSuccess() = code == "000000"
}