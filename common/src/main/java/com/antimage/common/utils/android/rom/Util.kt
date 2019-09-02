package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

/**
 * Created by xuyuming on 2019-08-27
 */
object Util {

    fun safeStart(context: Context, intent: Intent): Boolean = Util.safeStart(context, intent, true)

    fun safeStart(context: Context, intent: Intent, newTask: Boolean): Boolean {
        if (context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0) {
            if (newTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        } else {
            Log.e("rom Util", "Intent is not available! $intent")
            return false
        }
    }
}