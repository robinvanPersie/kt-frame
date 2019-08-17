package com.antimage.kt.presenter

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.antimage.basic.router.Router
import com.antimage.kt.di.MainApp
import com.antimage.kt.view.MainView

class MainPresenter: ModuleActivityPresenter<MainView>() {

    @JvmField
    @Autowired(name = Router.ModuleAppInit.APP_MODULE_INIT)
    var app: MainApp? = null

    override fun service() = app!!.apiService()


}