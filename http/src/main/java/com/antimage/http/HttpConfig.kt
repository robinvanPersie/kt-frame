package com.antimage.http

interface HttpConfig {

    fun getApiHost(): String

    fun getHeader(): Map<String, String>?
}