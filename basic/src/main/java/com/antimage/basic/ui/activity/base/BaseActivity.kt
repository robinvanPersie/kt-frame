package com.antimage.basic.ui.activity.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.antimage.basic.ui.fragment.dialog.LoadingDialogFragment
import com.antimage.basic.ui.view.IBaseView
import com.antimage.common.utils.android.ToastUtils
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function

abstract class BaseActivity : RxAppCompatActivity(), IBaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//  =============== override  =================

    override fun ensurePermission(permission: String, failMsg: String): Observable<Boolean>? {
        val rxPermission = RxPermissions(this)
        return rxPermission.requestEach(permission)
            .flatMap(object : Function<Permission, ObservableSource<Boolean>> {

                override fun apply(p: Permission): ObservableSource<Boolean> {
                    if (p.granted) {
                        return Observable.just(true)
                    } else if (!p.shouldShowRequestPermissionRationale) {
                        showToast(failMsg)
                        // OPEN SETTING
                    }
                    return Observable.just(false)
                }
            })
    }

    override fun ensurePermission(permission: String, failResId: Int)
            = ensurePermission(permission, getString(failResId))

    override fun showToast(text: String) {
        ToastUtils.showToast(text)
    }

    override fun showToast(resId: Int) {
        ToastUtils.showToast(resId)
    }

    override fun showLongToast(resId: Int) {
        ToastUtils.showLongToast(resId)
    }

    override fun showLongToast(text: String) {
        ToastUtils.showLongToast(text)
    }

    override fun showLoading() {
        this.showLoading("loading...")
    }

    override fun showLoading(text: String) {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag("loading_tag")
        if (fragment != null && fragment.isAdded && fragment is LoadingDialogFragment) {
//            var loadingDialogFragment: LoadingDialogFragment = fragment
            fragment.updateMessage(text.toString())
            return
        }
        LoadingDialogFragment.newInstance(text).show(supportFragmentManager, "loading_tag")
    }

    override fun showLoading(resId: Int) {
        this.showLoading(getString(resId))
    }

    override fun hideLoading() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("loading_tag")
        if (fragment != null && fragment is LoadingDialogFragment) {
            fragment.dismissAllowingStateLoss()
        }
    }
}