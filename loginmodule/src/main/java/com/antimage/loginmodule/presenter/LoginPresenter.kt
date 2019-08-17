package com.antimage.loginmodule.presenter

import android.Manifest
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.antimage.basic.router.Router
import com.antimage.http.Response
import com.antimage.loginmodule.api.ApiService
import com.antimage.loginmodule.di.LoginApp
import com.antimage.loginmodule.model.Config
import com.antimage.loginmodule.view.LoginView
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import timber.log.Timber

//class LoginPresenter : FragmentPresenter<LoginView>() {
class LoginPresenter : ModuleFragmentPresenter<LoginView>() {

    @JvmField
    @Autowired(name = Router.ModuleAppInit.LOGIN_MODULE_INIT)
    var app: LoginApp? = null

    override fun service(): ApiService {
        return app!!.apiService()
    }

    override fun onAttachView(v: LoginView) {
        super.onAttachView(v)
        open()
    }

    private fun open() {
        Timber.w("mV: $mV")
        var observable = mV?.ensurePermission(Manifest.permission.READ_EXTERNAL_STORAGE, "haha")
            ?.compose(bindLoading())
            ?.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            ?.flatMap(object : Function<Boolean, ObservableSource<Boolean>> {

                override fun apply(b: Boolean): Observable<Boolean> {
                    if (b) {
                        return mV?.ensurePermission(Manifest.permission.READ_PHONE_STATE, "imei") ?: Observable.empty()
                    }
                    mV?.showToast("storage no")
                    return Observable.empty()
                }
            })?.flatMap(object : Function<Boolean, ObservableSource<Response<Config>>> {

                override fun apply(t: Boolean): ObservableSource<Response<Config>> {
                    if (t) {
                      return mApiService?.appOpen() ?: Observable.error(Exception("appOpen return null"))
                    }
                    mV?.showToast("imei no")
                    return Observable.empty()
                }
            })?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                Log.e(javaClass.simpleName, "subscribe(): $it")
                Timber.w("subscribe(): " + it.data?.httpConfig?.api)
            }, {
                Timber.e(it.message)
            })
    }
}