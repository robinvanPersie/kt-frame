package com.antimage.loginmodule.presenter

import android.content.Context
import com.antimage.basic.presenter.base.FragmentPresenter
import com.antimage.loginmodule.api.ApiService
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent
import timber.log.Timber

/**
 * 每个module再定义一个自己的ModuleFragmentPresenter，用来管理自己的apiService等其他特有类
 */
abstract class ModuleFragmentPresenter<V>: FragmentPresenter<V>() {

    override fun isInject(): Boolean = true

    /**
     * 在父类使用@autowired注解字段，无法获取示例，仍然是null，暂时没找到原因
     * 不知是否和 open 或 abstract 修饰有关？
     */
//    @JvmField
//    @Autowired(name = Router.ModuleAppInit.LOGIN_MODULE_INIT)
//    var parentApp: LoginApp? = null

    protected lateinit var mApiService: ApiService

    override fun onCreate(context: Context, lifecycleProvider: LifecycleProvider<FragmentEvent>) {
        super.onCreate(context, lifecycleProvider)

//        Timber.w("login modulePresenter: parentService == null ? ${parentApp == null}")
        mApiService = service()
        Timber.e("login modulePresenter: apiService == null ? ${mApiService == null}")
    }

    abstract fun service(): ApiService

}