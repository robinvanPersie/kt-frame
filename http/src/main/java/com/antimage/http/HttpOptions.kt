package com.antimage.http

class HttpOptions private constructor(builder: Builder) : Options {

    companion object {
        val DEFAULT_OPTIONS: HttpOptions = Builder()
            .devBaseUrl("http://www.baidu.com")
            .releaseBaseUrl("http://www.baidu.com")
            .testBaseUrl("http://www.baidu.com")
            .build()
    }

//    @get: JvmName("-devBaseUrl")
    private val devBaseUrl = builder.devBaseUrl
//    @get: JvmName("-testBaseUrl")
    private val testBaseUrl = builder.testBaseUrl
//    @get: JvmName("-releaseBaseUrl")
    private val releaseBaseUrl = builder.releaseBaseUrl
//    @get: JvmName("-qa1BaseUrl")
    private val headers = builder.headers

    override fun devUrl(): String? = devBaseUrl

    override fun releaseUrl(): String? = releaseBaseUrl

    override fun testUrl(): String? = testBaseUrl

    override fun headers(): MutableMap<String, String>? = headers

    class Builder {
        internal var releaseBaseUrl: String? = null
        internal var devBaseUrl: String? = null
        internal var testBaseUrl: String? = null

        internal var headers: MutableMap<String, String>? = null

        fun releaseBaseUrl(url: String) = apply {
            this.releaseBaseUrl = url
        }

        fun devBaseUrl(url: String) = apply {
            this.devBaseUrl = url
        }

        fun testBaseUrl(url: String) = apply {
            this.testBaseUrl = url
        }

        fun headers(headers: MutableMap<String, String>) = apply {
            this.headers = headers
        }

        fun build(): HttpOptions = HttpOptions(this)
    }

}