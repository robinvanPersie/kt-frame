package com.antimage.basic.ui.fragment.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.antimage.basic.R
import com.antimage.basic.ui.activity.base.BaseActivity
import com.antimage.basic.ui.view.IBaseView
import com.antimage.common.utils.android.ToastUtils
import com.trello.rxlifecycle2.components.support.RxDialogFragment
import io.reactivex.Observable
import java.lang.Exception

open class BaseDialogFragment : RxDialogFragment(), IBaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragment_theme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (dialog == null || dialog.window == null) {
            showsDialog = false
            return
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        var clazz = DialogFragment::class.java
        try {
            val mDismissed = clazz.getDeclaredField("mDismissed")
            val mShownByMe = clazz.getDeclaredField("mShownByMe")
            mDismissed.isAccessible = true
            mShownByMe.isAccessible = true
            mDismissed.setBoolean(this, false)
            mShownByMe.setBoolean(this, true)
            manager.beginTransaction().add(0, this, tag).commit()
        } catch (e: Exception) {
            super.show(manager, tag)
        }
    }

//    ============== override ================

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
        (activity as? BaseActivity)?.showLoading(resId)
    }

    override fun hideLoading() {
        (activity as? BaseActivity)?.hideLoading()
    }

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
}