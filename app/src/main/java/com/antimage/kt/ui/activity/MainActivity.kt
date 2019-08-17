package com.antimage.kt.ui.activity

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.antimage.basic.router.Router
import com.antimage.basic.ui.activity.base.LifeCycleActivity
import com.antimage.common.utils.android.FragmentUtils
import com.antimage.kt.R
import com.antimage.kt.presenter.MainPresenter
import com.antimage.kt.view.MainView

class MainActivity : LifeCycleActivity<MainView, MainPresenter>(), MainView {

    override fun createPresenter(): MainPresenter = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var loginFragment = ARouter.getInstance().build(Router.Login.LOGIN_DIALOG_FRAGMENT).navigation() as DialogFragment
        FragmentUtils.show(supportFragmentManager, loginFragment)
    }
}
