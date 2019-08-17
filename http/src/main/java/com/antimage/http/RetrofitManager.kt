package com.antimage.http

import com.antimage.http.internal.Convert
import com.antimage.http.internal.GsonManager
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

internal class RetrofitManager(okHttpClient: OkHttpClient, appConfig: AppConfig) {

    private val retrofit: Retrofit

    init {
        val gson = GsonManager.instance.gson
        val converterFactory = Convert.ResponseConverterFactory.create(gson)
        val builder: Retrofit.Builder = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(appConfig.getApiHost())
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        retrofit = builder.build()
    }

    internal fun <S> create(clazz: Class<S>): S {
        return retrofit.create(clazz)
    }
}