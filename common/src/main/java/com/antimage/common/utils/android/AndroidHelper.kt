package com.antimage.common.utils.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.antimage.common.utils.android.rom.OppoUtil
import com.antimage.common.utils.android.rom.VivoUtil
import java.io.File

/**
 * Created by xuyuming on 2019-08-28
 */
object AndroidHelper {

    /**
     * 对于非网络uri发送意图
     * @return  true 成功跳转 false 没有执行跳转
     */
    fun openNativeUrl(context: Context, uri: Uri?): Boolean {
        if (uri == null) return false
        if ("http".equals(uri.scheme!!, ignoreCase = true) || "https".equals(uri.scheme!!, ignoreCase = true)) {
            //web url
            return false
        }
        val intent = Intent()
        intent.data = uri
        return safeStart(context, intent, false)
    }


    /**
     * 跳转到应用市场
     * @param context 上下文
     * @return true 成功跳转 false 没有跳转成功
     */
    fun jumpToAppMarket(context: Context): Boolean {
        val address = "market://details?id=" + context.packageName
        val marketIntent = Intent(Intent.ACTION_VIEW)
        marketIntent.data = Uri.parse(address)
        return if (context.packageManager.queryIntentActivities(marketIntent, PackageManager.MATCH_DEFAULT_ONLY).size > 0) {
            context.startActivity(marketIntent)
            true
        } else {
            false
        }
    }


    /**
     * 显示拨号界面
     * @param context 上下文
     * @param number 电话号码
     */
    fun showDial(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$number")
        intent.data = data
        context.startActivity(intent)
    }


    /**
     * 安装应用
     *
     * @param file apk文件目录
     */
    fun installApp(context: Context, file: File) {
        if (file.exists()) {
            context.startActivity(getInstallIntent(context, file))
        }
    }

    /**
     * 获取安装文件的intent意图
     * @param context
     * @param file apk文件目录
     * @return
     */
    fun getInstallIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 24) {
            val apkUri = getFileProviderSharedUri(context, file)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        return intent
    }


    private fun getFileProviderSharedUri(context: Context, file: File): Uri {
        val packageName = context.packageName
        val authority = "$packageName.FileProvider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    /**
     * 跳转到应用设置详情页面
     * @param context 上下文
     */
    fun jumpToAppDetailsSetting(context: Context): Boolean {
        return safeStart(
            context,
            getAppDetailSettingsIntent(context),
            false
        )
    }

    /**
     * 条撞到应用权限设置页面
     * @param context 上下文
     * @return true 成功跳转 false 失败
     */
    fun jumpToPermissionSettings(context: Context): Boolean {
        //        if(OppoUtil.isOppo() && OppoUtil.openPermissionSettings(context)){
        //            return true;
        //        }
        //        if(VivoUtil.isVivo() && VivoUtil.openPermissionSettings(context)){
        //            return true;
        //        }

        if (OppoUtil.isOppo()) {
            return OppoUtil.openPermissionSettings(context)
        }
        return if (VivoUtil.isVivo()) {
            VivoUtil.openPermissionSettings(context)
        } else jumpToAppDetailsSetting(context)

    }


    fun jumpToLocationEnable(context: Context): Boolean {
        return safeStart(
            context,
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
            false
        )
    }


    /**
     * 跳转到应用设置详情页面
     * @param activity host
     * @param requestCode 标识
     */
    fun jumpToAppDetailsSetttingForResult(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(
            getAppDetailSettingsIntent(
                activity
            ), requestCode)
    }

    /**
     * 跳转到应用设置详情页面
     * @param fragment host
     * @param requestCode 标识
     */
    fun jumpToAppDetailsSettingForResult(fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(
            getAppDetailSettingsIntent(
                fragment.context
            ), requestCode)
    }

    private fun getAppDetailSettingsIntent(context: Context?): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        return intent
    }


    fun pickContact(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = ContactsContract.Contacts.CONTENT_URI
        return intent
    }

    fun parserPickContactResult(context: Context, data: Intent): Pair<String, String>? {
        var displayName: String
        var phoneNumber = ""
        val uri = data.data
        val c = context.contentResolver.query(uri!!, null, null, null, null)
        if (c != null && c.moveToFirst()) {
            displayName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val phoneNum = c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            if (phoneNum > 0) {
                val contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                val phone = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null,
                    null
                )
                if (phone != null && phone.moveToFirst()) {
                    while (!phone.isAfterLast) {
                        val index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val typeIndex = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                        val phone_type = phone.getInt(typeIndex)//phone_type 2 为手机
                        phoneNumber = phone.getString(index).replace(" ".toRegex(), "")
                        if (phone_type == 2) {//是手机号 跳出循环
                            break
                        }
                        phone.moveToNext()
                    }
                    if (!phone.isClosed) {
                        phone.close()
                    }
                }
            }
            c.close()
        } else {
            return null
        }
        return Pair(displayName, phoneNumber)
    }

    /**
     * 如果有activity接收意图则打开新的activity
     * @param activity 当前activity
     * @param intent 意图
     * @param requestCode 请求code
     * @return true 成功打开 false 没有接收的activity
     */
    fun safeStartActivityForResult(activity: Activity, intent: Intent, requestCode: Int): Boolean {
        if (activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0) {
            try {
                activity.startActivityForResult(intent, requestCode)
                return true
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.toString())
                return false
            }
        } else {
            Log.e(javaClass.simpleName, "no intent match")
            return false
        }
    }

    /**
     * 确保系统有接收意图
     * @param context 上下文
     * @param intent 意图
     */
    fun safeStart(context: Context, intent: Intent): Boolean {
        return safeStart(context, intent, false)
    }

    private fun safeStart(context: Context, intent: Intent, newTask: Boolean): Boolean {
        if (context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0) {
            if (newTask) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.toString())
                return false
            }

            return true
        } else {
            Log.e(javaClass.simpleName, "no intent to be matched")
            return false
        }
    }
}