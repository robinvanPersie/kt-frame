package com.antimage.kt.presenter

import android.Manifest
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.antimage.basic.router.Router
import com.antimage.kt.di.MainApp
import com.antimage.kt.view.LauncherView
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable


internal class LauncherPresenter: ModuleActivityPresenter<LauncherView>() {

    @JvmField
    @Autowired(name = Router.ModuleAppInit.APP_MODULE_INIT)
    var app: MainApp? = null

    override fun service() = app!!.apiService()

    override fun onAttachView(v: LauncherView) {
        super.onAttachView(v)
        open()
    }

    private fun open() {
        mV?.ensurePermission(Manifest.permission.READ_PHONE_STATE, "please allow")
            ?.compose(bindUntilEvent(ActivityEvent.DESTROY))
            ?.flatMap{
                if (it)
                    mApiService.appOpen()
                else
                    Observable.empty()
            }
            ?.subscribe({
                mV?.openAppHomePage()
            }, {})

    }
}