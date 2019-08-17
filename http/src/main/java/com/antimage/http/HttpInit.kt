package com.antimage.http

import android.content.Context

object HttpInit {

    fun install(context: Context, options: Options) {
//        var options: HttpOptions = HttpOptions.Builder()
//            .releaseBaseUrl(HttpParams.RELEASE_URL)
//            .devBaseUrl(HttpParams.DEV_URL)
//            .testBaseUrl(HttpParams.DEV_URL)
//            .qa1BaseUrl(HttpParams.DEV_URL)
//            .build()
        buildAppConfig(context, options).buildHttpManager(context)
    }

    lateinit var appConfig: AppConfig
        private set
    lateinit var httpManager: HttpManager
        private set

    private fun buildAppConfig(context: Context, httpOptions: Options) = apply {
        appConfig = AppConfigImpl.getInstance(context, httpOptions)
    }

    private fun buildHttpManager(context: Context) {
        httpManager = HttpManager.getInstance(context, appConfig)
    }


}