package com.antimage.common.utils.android

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Created by xuyuming on 2019-08-28
 */
object PermissionUtils {

    /**
     * 判断是否有目标权限
     * @param context
     * @param permission
     * @return
     */
    fun hasTargetPermission(context: Context, permission: String) =
        ActivityCompat.checkSelfPermission(context, permission) === PackageManager.PERMISSION_GRANTED
}