package com.antimage.basic.ui.fragment.base

import com.antimage.basic.ui.activity.base.BaseActivity
import com.antimage.basic.ui.view.FragmentCallback
import com.antimage.basic.ui.view.IBaseView
import com.antimage.common.utils.android.ToastUtils
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.Observable

open class BaseFragment : RxFragment(), IBaseView, FragmentCallback {

//  ============ override ==============

    override fun onFragmentBackPress(): Boolean = false

    override fun showToast(resId: Int) {
        ToastUtils.showToast(resId)
    }

    override fun showToast(text: String) {
        ToastUtils.showToast(text)
    }

    override fun showLongToast(resId: Int) {
        ToastUtils.showLongToast(resId)
    }

    override fun showLongToast(text: String) {
        ToastUtils.showLongToast(text)
    }

    override fun ensurePermission(permission: String, failMsg: String): Observable<Boolean>?
            = (activity as BaseActivity).ensurePermission(permission, failMsg)

    override fun ensurePermission(permission: String, failResId: Int)
            = this.ensurePermission(permission, getString(failResId))


    override fun showLoading() {
        this.showLoading("loading...")
    }

    override fun showLoading(text: String) {
        (activity as? BaseActivity)?.showLoading(text)
    }

    override fun showLoading(resId: Int) {
        this.showLoading(getString(resId))
    }

    override fun hideLoading() {
        (activity as? BaseActivity)?.hideLoading()
    }
}