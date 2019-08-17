package com.antimage.basic.ui.view

interface PresenterProvide<P> {

    fun createPresenter(): P
}