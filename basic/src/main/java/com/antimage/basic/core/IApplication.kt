package com.antimage.basic.core

import com.alibaba.android.arouter.facade.template.IProvider

interface IApplication : IProvider {

    fun onCreate()

    fun get(): IApplication
}