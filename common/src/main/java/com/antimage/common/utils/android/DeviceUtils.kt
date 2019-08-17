package com.antimage.common.utils.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import java.lang.Exception

object DeviceUtils {

    fun getVersionName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    }

    fun getVersionCode(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0)
                .longVersionCode.toString()
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
                .versionCode.toString()
        }
    }

    fun getOSVersion(): String = Build.VERSION.RELEASE

    @SuppressLint("HardwareIds")
    fun getImei(context: Context): String {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                val telephonyManager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei?: telephonyManager.deviceId
                } else {
                    telephonyManager.deviceId
                }
            }
        } catch (e: Exception) {
//            e.printStackTrace();
        }
        return ""
    }
}