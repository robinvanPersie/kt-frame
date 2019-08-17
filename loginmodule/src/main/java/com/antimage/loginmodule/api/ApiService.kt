package com.antimage.loginmodule.api

import com.antimage.http.Response
import com.antimage.loginmodule.model.Config
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {

    @Headers("baseurl: cs")
    @GET("app/open")
    fun appOpen(): Observable<Response<Config>>
}