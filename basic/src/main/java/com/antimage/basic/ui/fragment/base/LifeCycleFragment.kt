package com.antimage.basic.ui.fragment.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.antimage.basic.presenter.base.FragmentPresenter
import com.antimage.basic.ui.view.PresenterProvide

abstract class LifeCycleFragment<V, P : FragmentPresenter<V>> : BaseFragment(), PresenterProvide<P> {

    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter.onCreate(activity as Context, this)
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