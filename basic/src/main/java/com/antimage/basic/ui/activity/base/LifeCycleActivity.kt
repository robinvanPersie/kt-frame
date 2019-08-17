package com.antimage.basic.ui.activity.base

import android.os.Bundle
import com.antimage.basic.presenter.base.ActivityPresenter
import com.antimage.basic.ui.view.IBaseView
import com.antimage.basic.ui.view.PresenterProvide

abstract class LifeCycleActivity<V, P : ActivityPresenter<V>>: BaseActivity(), PresenterProvide<P> {

    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter.onCreate(this, this)
//        mPresenter.onAttachView(mPresenter.mV!!)
        mPresenter.onAttachView(this as V)
    }

    override fun onDestroy() {
        mPresenter.onDetachViw()
        mPresenter.onDestroy()
        super.onDestroy()
    }
}