package com.antimage.kt.api

import com.antimage.http.Response
import com.antimage.kt.model.Config
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

interface AppApiService {

    @Headers("baseurl: cs")
    @GET("app/open")
    fun appOpen(): Observable<Response<Config>>
}