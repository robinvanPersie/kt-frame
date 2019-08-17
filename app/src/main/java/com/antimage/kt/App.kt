package com.antimage.kt

import android.app.Application
import com.antimage.basic.BasicApp
import com.antimage.http.HttpInit
import com.antimage.http.HttpOptions
import com.antimage.basic.core.HttpParams

class App : BasicApp() {

//    companion object {
//        var instance: App? = null
//            private set
//    }

    override fun onCreate() {
        initHttp()
        super.onCreate()
//        instance = this

    }

    private fun initHttp() {
        var options: HttpOptions = HttpOptions.Builder()
            .releaseBaseUrl(HttpParams.RELEASE_URL)
            .devBaseUrl(HttpParams.DEV_URL)
            .testBaseUrl(HttpParams.DEV_URL)
            .build()
        HttpInit.install(this, options)
    }
}