package com.antimage.basic.di

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.DegradeService
import timber.log.Timber

@Route(path = "/basic/randomlost")
class DegradeServiceImpl : DegradeService {

    override fun init(context: Context?) {
    }

    override fun onLost(context: Context?, postcard: Postcard) {
        Timber.w("全局降级策略，location: basic module, DegradeServiceImpl.kt")
    }

}