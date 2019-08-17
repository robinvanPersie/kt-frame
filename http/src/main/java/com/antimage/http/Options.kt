package com.antimage.http

interface Options {

    fun devUrl(): String?

    fun releaseUrl(): String?

    fun testUrl(): String?

    fun headers(): MutableMap<String, String>?

}
