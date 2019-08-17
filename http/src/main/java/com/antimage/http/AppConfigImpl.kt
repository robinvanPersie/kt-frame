package com.antimage.http

import android.content.Context
import android.content.SharedPreferences
import com.antimage.common.utils.android.DeviceUtils
import java.io.File

class AppConfigImpl private constructor(private val context: Context, httpOptions: Options) : AppConfig {

    companion object {
        private const val SP_NAME: String = "app_config"
        private const val ENV_LOCAL: String = "env_local"
        private const val HTTP_CACHE_DIR = "http"

        @Volatile
        private var instance: AppConfigImpl? = null

        fun getInstance(context: Context, httpOptions: Options): AppConfigImpl {
            return instance ?: synchronized(AppConfigImpl::class.java) {
                instance
                    ?: AppConfigImpl(context, httpOptions).also { instance = it }
            }
        }
    }

    private val mSharedPreference: SharedPreferences =
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    private val env: Int

    private var releaseBaseUrl: String = httpOptions.releaseUrl()!!
    private var devBaseUrl: String = httpOptions.devUrl()!!
    private var testBaseUrl: String = httpOptions.testUrl() ?: ""
    private var headers: Map<String, String>?

    init {
        headers = httpOptions.headers() ?: getDefaultHeader()
        env = environment()
    }

    private fun environment(): Int {
        var env = mSharedPreference.getInt(ENV_LOCAL, -1)
        return if (env == -1) initializeEnv() else env
    }

    private fun initializeEnv(): Int {
        val env = when (BuildConfig.BUILD_TYPE) {
            "release" -> 0
            "debug" -> 1
            "test" -> 2
            else -> 0
        }
        saveEnvironment(env)
        return env
    }


//    ************* override *******************

    override fun getAppName(): String = "kt-first"

    override fun getDeviceId(): String = "123456789asdfghj"

    override fun saveEnvironment(env: Int) {
        mSharedPreference.edit().putInt(ENV_LOCAL, env).apply()
    }

    override fun getHttpExternalCache(): String
            = String.format("%s%s%s", context.externalCacheDir, File.separator, HTTP_CACHE_DIR).also(::println)

    override fun getApiHost(): String {
        return when (env) {
            0 -> releaseBaseUrl
            1 -> devBaseUrl
            2 -> testBaseUrl
            else -> releaseBaseUrl
        }
    }

    private fun getDefaultHeader(): Map<String, String> = mutableMapOf(
        "App-Name" to getAppName(),
        "App-Version" to DeviceUtils.getVersionName(context),
        "token" to "",
        "OS-Version" to DeviceUtils.getOSVersion(),
        "Version-Code" to DeviceUtils.getVersionCode(context),
        "imei" to DeviceUtils.getImei(context),
        "Device-ID" to getDeviceId()
    )

    override fun getHeader(): Map<String, String>? {
        if (headers is MutableMap) {
            var h = headers as MutableMap
            h["token"] = ""
            h["imei"] = DeviceUtils.getImei(context)
            h["Device-ID"] = getDeviceId()
            headers = h
        }
        return headers
    }
}