package com.antimage.basic.ui.fragment.base

import android.os.Bundle
import android.view.View
import com.antimage.basic.presenter.base.FragmentPresenter
import com.antimage.basic.ui.view.PresenterProvide

abstract class LifeCycleDialogFragment<V, P : FragmentPresenter<V>> : BaseDialogFragment(), PresenterProvide<P> {

    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter.onCreate(context!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onAttachView(this as V)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onDetachViw()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }
}