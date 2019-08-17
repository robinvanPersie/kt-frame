package com.antimage.loginmodule.di

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.antimage.basic.core.IApplication
import com.antimage.basic.router.Router
import com.antimage.http.HttpInit
import com.antimage.loginmodule.api.ApiService
import timber.log.Timber

@Route(path = Router.ModuleAppInit.LOGIN_MODULE_INIT)
class LoginApp : IApplication {

    private lateinit var mApiService: ApiService

    override fun onCreate() {
        Timber.e("login app onCreate()")
        mApiService = HttpInit.httpManager.create(ApiService::class.java)
    }

    override fun get(): IApplication = this

    override fun init(context: Context) {
        Timber.w("login app init()")
    }

    fun apiService(): ApiService = mApiService

}