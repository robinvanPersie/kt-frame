package com.antimage.kt.ui.activity

import android.content.Intent
import android.os.Bundle
import com.antimage.basic.ui.activity.base.LifeCycleActivity
import com.antimage.common.helper.WindowHelper
import com.antimage.kt.presenter.LauncherPresenter
import com.antimage.kt.view.LauncherView


internal class LauncherActivity: LifeCycleActivity<LauncherView, LauncherPresenter>(), LauncherView {

    override fun createPresenter(): LauncherPresenter {
        return LauncherPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowHelper.hideBottomNav(window)
    }

    /**
     * 进入首页
     */
    override fun openAppHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}