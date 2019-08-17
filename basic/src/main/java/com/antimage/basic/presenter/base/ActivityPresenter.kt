package com.antimage.basic.presenter.base

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent

abstract class ActivityPresenter<V> : BasePresenter<ActivityEvent, V>() {

    override fun onCreate(context: Context, lifecycleProvider: LifecycleProvider<ActivityEvent>) {
        super.onCreate(context, lifecycleProvider)
        if (isInject()) {
            ARouter.getInstance().inject(this)
        }
    }

    protected abstract fun isInject(): Boolean
}