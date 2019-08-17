package com.antimage.http

import android.content.Context
import com.antimage.http.internal.MultipleUrlInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 懒汉线程安全
 * Java:    private static HttpManager instance;
 *
 *          private HttpManager() {}
 *
 *          public static HttpManager getInstance() {
 *            if (instance == null) {
 *              synchronized (HttpManager.class) {
 *                if (instance == null) {
 *                  HttpManager ins = new HttpManager();
 *                  instance = ins;
 *                }
 *              }
 *            }
 *            return instance;
8          }
 */
class HttpManager private constructor(context: Context, appConfig: AppConfig) {

    companion object {
//        val instance: HttpManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED, {HttpManager()})
//        lambda参数应该移出括号，所以变成了下面这行写法
//        val instance: HttpManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {HttpManager()}


        @Volatile
        private var instance: HttpManager? = null

        fun getInstance(ctx: Context, appConfig: AppConfig) =
            instance ?: synchronized(HttpManager::class.java) {
                instance ?: HttpManager(ctx, appConfig).also {
                    instance = it
                }
            }
    }

    private val okHttpClient: OkHttpClient
    private val retrofitManager: RetrofitManager

    init {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addNetworkInterceptor(MultipleUrlInterceptor(context, appConfig))
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .cache(Cache(File(appConfig.getHttpExternalCache()), 1024 * 1024 * 10))
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        okHttpClient = builder.build()

        retrofitManager = RetrofitManager(okHttpClient, appConfig)
    }

    fun <S> create(clazz: Class<S>): S {
        return retrofitManager.create(clazz)
    }
}