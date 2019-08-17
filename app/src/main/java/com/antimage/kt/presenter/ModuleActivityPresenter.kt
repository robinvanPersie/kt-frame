package com.antimage.kt.presenter

import android.content.Context
import com.antimage.basic.presenter.base.ActivityPresenter
import com.antimage.kt.api.AppApiService
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent

abstract class ModuleActivityPresenter<V>: ActivityPresenter<V>() {

    override fun isInject(): Boolean = true

    protected lateinit var mApiService: AppApiService

    override fun onCreate(context: Context, lifecycleProvider: LifecycleProvider<ActivityEvent>) {
        super.onCreate(context, lifecycleProvider)
//        mApiService = app?.apiService()
//        Timber.e("app modulePresenter: apiService == null ? ${mApiService == null}")
        mApiService = service()
    }

    abstract fun service(): AppApiService
}