package com.antimage.http.internal

import android.content.Context
import android.text.TextUtils
import com.antimage.http.AppConfig
import com.antimage.common.utils.android.NetWorkUtils
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

internal class MultipleUrlInterceptor constructor(context: Context, appConfig: AppConfig) : Interceptor {

    private val mAppConfig = appConfig
    private val mContext = context

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest : Request = chain.request()
        val requestBuilder: Request.Builder = originalRequest.newBuilder()
        val originalHeaders: Headers = originalRequest.headers

        var oldUrl: HttpUrl = originalRequest.url
        var newUrl: HttpUrl? = null
        val baseUrls = originalHeaders.values("baseurl")
        if (baseUrls.isNotEmpty()) {
            requestBuilder.removeHeader("baseurl")
            var base = baseUrls[0]
            var baseUrl: HttpUrl = if ("cs" == base) oldUrl else mAppConfig.getApiHost().toHttpUrlOrNull()!!

            newUrl = oldUrl.newBuilder()
                .scheme(baseUrl.scheme)
                .host(baseUrl.host)
                .port(baseUrl.port)
                .build()
        }

        val apiHeader = mAppConfig.getHeader()
        if (apiHeader != null && apiHeader.isNotEmpty()) {
            apiHeader.forEach {
                requestBuilder.header(it.key, it.value)
            }
        }

        var request: Request
        requestBuilder.method(originalRequest.method, originalRequest.body)
        if (newUrl != null) requestBuilder.url(newUrl)
        request = requestBuilder.build()

        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            if (request.cacheControl.isPublic) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            }
        }
        var response = chain.proceed(request)
        return if (NetWorkUtils.isNetworkAvailable(mContext)) {
            var headValue = originalRequest.cacheControl.toString()
            if (!TextUtils.isEmpty(headValue)) {
                response = response.newBuilder().header("Cache-Control", headValue).removeHeader("Pragma").build()
            }
            response
        } else {
            response.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                .removeHeader("Pragma")
                .build()
        }
    }
}