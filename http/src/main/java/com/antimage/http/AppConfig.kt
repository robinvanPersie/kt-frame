package com.antimage.http

interface AppConfig : HttpConfig {

    fun saveEnvironment(env: Int)

    fun getAppName(): String

    fun getDeviceId(): String

    fun getHttpExternalCache(): String
}