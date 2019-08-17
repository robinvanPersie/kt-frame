package com.antimage.basic

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.antimage.basic.core.HttpParams
import com.antimage.basic.core.IApplication
import com.antimage.basic.router.Router
import com.antimage.http.HttpInit
import com.antimage.http.HttpOptions
import com.antimage.http.Options
import timber.log.Timber

open class BasicApp : Application() {

    companion object {

        var instance: BasicApp? = null
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Timber.w("app attachBaseContext()")
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
//        initHttp(this)

        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        ARouter.getInstance().inject(this)
        initModuleConfiguration()
    }

    /**
     * 设置为可以为null，利于module单独运行不会报npe
     */
    private fun initModuleConfiguration() {
        val loginApp: IApplication? = ARouter.getInstance().build(Router.ModuleAppInit.LOGIN_MODULE_INIT).navigation() as? IApplication
        loginApp?.onCreate()
        val mainApp: IApplication? = ARouter.getInstance().build(Router.ModuleAppInit.APP_MODULE_INIT).navigation() as? IApplication
        mainApp?.onCreate()
    }

    private fun initHttp(context: Context) {
        var options: Options = HttpOptions.Builder()
            .releaseBaseUrl(HttpParams.RELEASE_URL)
            .devBaseUrl(HttpParams.DEV_URL)
            .testBaseUrl(HttpParams.DEV_URL)
            .qa1BaseUrl(HttpParams.DEV_URL)
            .build()
        HttpInit.install(context, options)
    }
}
