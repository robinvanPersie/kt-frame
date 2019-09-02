package com.antimage.common.utils.android

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Created by xuyuming on 2019-08-28
 */
object NetUtils {

    const val NET_NONE = 0 // 无网
    const val NET_WIFI = 1 // WIFI
    const val NET_2G = 2 // 2G
    const val NET_3G = 3 // 3G
    const val NET_4G = 4 // 3G
    const val NET_OTHER = 5 // 其他

    @IntDef(NET_NONE, NET_WIFI, NET_2G, NET_3G, NET_4G, NET_OTHER)
    @Retention(RetentionPolicy.SOURCE)
    private annotation class NetState


    private const val MOBILE_NETWORK_2G = 1
    private const val MOBILE_NETWORK_3G = 2
    private const val MOBILE_NETWORK_4G = 3
    private const val MOBILE_NETWORK_UNKNOWN = 4
    private const val MOBILE_NETWORK_DISCONNECT = 5

    @IntDef(MOBILE_NETWORK_2G, MOBILE_NETWORK_3G, MOBILE_NETWORK_4G, MOBILE_NETWORK_UNKNOWN, MOBILE_NETWORK_DISCONNECT)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class MobileNetState


    // permission android.permission.internet
    /**
     * 获取wifi mac 地址
     * @return
     */
    fun getWifiMacAddress(): String {
        try {
            val interfaceName = "wlan0"
            val interfaces = Collections
                .list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (!intf.name.equals(interfaceName, ignoreCase = true)) {
                    continue
                }

                val mac = intf.hardwareAddress ?: return ""

                val buf = StringBuilder()
                for (aMac in mac) {
                    buf.append(String.format("%02X:", aMac))
                }
                if (buf.isNotEmpty()) {
                    buf.deleteCharAt(buf.length - 1)
                }
                return buf.toString()
            }
        } catch (exp: Exception) {

            exp.printStackTrace()
        }

        return ""
    }

    /**
     * 获取本地Ip地址
     * @return
     */
    fun getLocalIpAddress(): String {
        try {
            if (NetworkInterface.getNetworkInterfaces() != null) {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    if (intf != null) {
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                if (inetAddress.getHostAddress() != "null" && inetAddress.getHostAddress() != null) {
                                    return inetAddress.getHostAddress().trim { it <= ' ' }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }

        return ""
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    fun getMACAddress(interfaceName: String?): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (idx in mac.indices)
                    buf.append(String.format("%02X:", mac[idx]))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
    }

    /**
     * 获取Ip地址
     * @param useIPv4 是否使用 ipv4
     * @return ip地址的字符串返回
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
    }


    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {

        } else {
            val networkInfo = connectivity.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    /**
     * 是否为wifi环境
     * @param context
     * @return
     */
    fun isWifiNetwork(context: Context?): Boolean {
        if (context == null) return false
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
        val wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifi != null && wifi.isAvailable && wifi.isConnected
    }


    /**
     * 打开或关闭wifi
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    /*public static void setWifiEnabled(Context context,boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }*/

    /**
     * 检测手机环境
     * @param context
     * @return
     */
    @MobileNetState
    fun checkMobileNetwork(context: Context?): Int {
        if (context == null) {
            return MOBILE_NETWORK_UNKNOWN
        }
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return if (mobile != null && mobile.isAvailable && mobile.isConnected) {
            when (mobile.subtype) {
                TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS -> MOBILE_NETWORK_2G
                TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSPAP -> MOBILE_NETWORK_3G
                TelephonyManager.NETWORK_TYPE_LTE -> MOBILE_NETWORK_4G
                else -> MOBILE_NETWORK_UNKNOWN
            }
        } else {
            MOBILE_NETWORK_DISCONNECT
        }
    }


    /**
     * 返回网络类型
     *
     * @return
     */
    @NetState
    fun getNetworkState(context: Context): Int {
        var net = NET_OTHER
        try {
            if (!isNetworkAvailable(context)) {
                // 当前未联网
                net = NET_NONE
            } else if (isWifiNetwork(context)) {
                // 当前为WIFI
                net = NET_WIFI
            } else {
                when (checkMobileNetwork(context)) {
                    MOBILE_NETWORK_2G -> net = NET_2G
                    MOBILE_NETWORK_3G -> net = NET_3G
                    MOBILE_NETWORK_4G -> net = NET_4G
                    MOBILE_NETWORK_DISCONNECT -> net = NET_NONE
                    MOBILE_NETWORK_UNKNOWN -> net = NET_OTHER
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return net
    }


    /**
     * 打开网络设置界面
     */
    fun openWirelessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


    /**
     * 判断移动数据是否打开
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun getDataEnabled(context: Context): Boolean {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
            if (null != getMobileDataEnabledMethod) {
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


    /**
     * 打开或关闭移动数据
     *
     * 需系统应用 需添加权限`<uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>`
     *
     * @param enabled `true`: 打开<br></br>`false`: 关闭
     */
    fun setDataEnabled(context: Context, enabled: Boolean) {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val setMobileDataEnabledMethod =
                tm.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
            setMobileDataEnabledMethod?.invoke(tm, enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}