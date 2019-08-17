package com.antimage.loginmodule.model

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("domains") val httpConfig: HttpConfig
) {

    data class HttpConfig(
        val api: String,
        val h5: String,
        @SerializedName("xyf_api") val xyfApi: String,
        @SerializedName("xyf_h5") val xyfH5: String
    )
}