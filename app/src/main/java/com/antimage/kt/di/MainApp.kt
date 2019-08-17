package com.antimage.kt.di

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.antimage.basic.core.IApplication
import com.antimage.basic.router.Router
import com.antimage.http.HttpInit
import com.antimage.kt.api.AppApiService
import timber.log.Timber

@Route(path = Router.ModuleAppInit.APP_MODULE_INIT)
class MainApp: IApplication {

    private lateinit var mApiService: AppApiService

    override fun onCreate() {
        mApiService = HttpInit.httpManager.create(AppApiService::class.java)
    }

    override fun get(): IApplication = this

    override fun init(context: Context?) {
        Timber.w("main app init()")
    }

    fun apiService() = mApiService
}