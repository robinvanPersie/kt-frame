package com.antimage.common.utils.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import java.io.*
import java.lang.Exception
import java.lang.NumberFormatException

object DeviceUtils {

    private var sMaxCpuFreq = 0L
    private var sTotalMemory = 0

    /**
     * 手机cpu主频大小
     */
    fun getMaxCpuFreq(): Long {
        if (sMaxCpuFreq > 0) {
            return sMaxCpuFreq
        }
        var cmd: ProcessBuilder
        var cpuFreq = StringBuilder()
        try {
            var args = listOf("/system/bin/cat", "sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            cmd = ProcessBuilder(args)
            var process = cmd.start()
            var ins = process.inputStream
            val re = ByteArray(24)
            while (ins.read(re) != -1) {
                cpuFreq.append(String(re))
            }
            ins.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            cpuFreq.clear()
        }
        var cpuFreqStr = cpuFreq.toString().trim()
        if (cpuFreqStr.isEmpty()) {
            // 某些机器取到的是空字符串，如：OPPO U701
            sMaxCpuFreq = 1
        } else {
            try {
                sMaxCpuFreq = cpuFreqStr.toLong()
            } catch (e: NumberFormatException) {
                sMaxCpuFreq = 1
                e.printStackTrace()
            }
        }
        return sMaxCpuFreq
    }

    fun getTotalMemoryInMb() = getTotalMemoryInKb() shr 10

    /**
     * 获取总内存:kb
     */
    fun getTotalMemoryInKb(): Int {
        if (sTotalMemory > 0) {
            return sTotalMemory
        }
        val str1 = "/proc/meminfo"
        var arrayOfString: Array<String>
        var fr: FileReader? = null
        var localBufferedReader: BufferedReader? = null
        try {
            fr = FileReader(str1)
            localBufferedReader = BufferedReader(fr, 8192)
            var lines = localBufferedReader.readLines()
            for (line in lines) {
                arrayOfString = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                sTotalMemory = Integer.valueOf(arrayOfString[1]).toInt()
            }
        } catch (e: IOException) {

        } finally {
            try {
                fr?.close()
                localBufferedReader?.close()
            } catch (e: IOException) {
            }

        }
        return sTotalMemory
    }

    /**
     * free内存
     */
    fun getFreeMemoryInKb(): Int {
        val str1 = "/proc/meminfo"
        val arrayOfString: Array<String>
        var fr: FileReader? = null
        var localBufferedReader: BufferedReader? = null
        var freeMem = 0
        try {
            fr = FileReader(str1)
            localBufferedReader = BufferedReader(fr, 8192)
            var lines = localBufferedReader.readLines()
            for ((l, line) in lines.withIndex()) {
                if (l == 2) {
                    arrayOfString = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    freeMem = Integer.valueOf(arrayOfString[1]).toInt()
                    break
                }
            }
        } catch (e: IOException) {

        } finally {
            try {
                fr?.close()
                localBufferedReader?.close()
            } catch (e: IOException) {
            }
        }
        return freeMem
    }

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

    /**
     * 手机系统版本号
     * e.g: 7.1.1
     */
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

    /**
     * 获取当前手机系统的最大的可用堆栈
     */
    fun getMaxHeapSizeInBytes(context: Context): Long {
        var max = Runtime.getRuntime().maxMemory()
        try {
            var am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var memoryClass: Long = (am.memoryClass shl 20).toLong()
            if (max > memoryClass) {
                max = memoryClass
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return max
    }

    /**
     * 获取当前已分配的堆栈比例
     */
    fun getHeapAllocatePercent(): Float {
        var heapAllocated = Runtime.getRuntime().totalMemory()
        var heapMax = Runtime.getRuntime().maxMemory()
        return Math.round(heapAllocated * 10000f / heapMax) / 100f
    }

    /**
     * 获取当前已使用的堆栈占总堆栈比例
     */
    fun getHeapUsedPercent(context: Context): Float {
        var heapUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        var heapMax = getMaxHeapSizeInBytes(context)
        return Math.round(heapUsed * 10000f / heapMax) / 100f
    }

    fun getCpuHardware(): String {
        var r = ""
        var pb: ProcessBuilder = ProcessBuilder("/system/bin/cat", "/proc/cpuinfo")
        try {
            var process = pb.start()
            var insr = InputStreamReader(process.inputStream)
            var lines = insr.buffered().readLines()
            for (line in lines) {
                if (line.contains("Hardware", true)) {
                    val a = line.split(":".toRegex(), 2).toTypedArray()
                    r = a[1]
                }
            }
            insr.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return r
    }

    /**
     * 获取cpu名称
     */
    fun getCpuName(): String {
        var result: String?
        result = getArmCPUName()
        if (result == null) {
            result = getX86CPUName()
        }
        if (result == null) {
            result = getMIPSCPUName()
        }
        return result ?: ""
    }

    private fun getX86CPUName(): String? {
        var aLine = "Intel"
        var file = File("/proc/cpuinfo")
        if (file.exists()) {
            try {
                var br = file.bufferedReader()
                var strArray: Array<String>
                var lines = br.readLines()
                for (line in lines) {
                    if (line.contains("model name")) {
                        br.close()
                        strArray = aLine.split(":".toRegex(), 2).toTypedArray()
                        aLine = strArray[1]
                    }
                }
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return aLine
    }

    private fun getMIPSCPUName(): String? {
        var aLine = "MIPS"
        var file = File("/proc/cpuinfo")
        if (file.exists()) {
            try {
                val br = file.bufferedReader()
                var strArray: Array<String>
                var lines = br.readLines()
                for (line in lines) {
                    if (line.contains("cpu model")) {
                        br.close()
                        strArray = aLine.split(":".toRegex(), 2).toTypedArray()
                        aLine = strArray[1]
                    }
                }
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return aLine
    }

    private fun getArmCPUName(): String? {
        try {
            val br = File("/proc/cpuinfo").bufferedReader()
            val text = br.readLine()
            br.close()
            val array = text.split(":\\s+".toRegex(), 2).toTypedArray()
            if (array.size >= 2) {
                return array[1]
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 判断手机是否已root
     *
     * @return
     */
    fun isRooted(): Boolean {
        return findBinary("su")
    }

    private fun findBinary(binaryName: String): Boolean {
        var found = false
        if (!found) {
            val places = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
            )
            for (where in places) {
                if (File(where + binaryName).exists()) {
                    found = true
                    break
                }
            }
        }
        return found
    }

    /**
     * 判断是否开启位置模拟
     *
     * @param context
     * @return 是否开启位置模拟
     */
    fun isMockLocationEnabled(context: Context): Boolean {
        var isMockLocation = false
        try {
            //if marshmallow
            isMockLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                opsManager.checkOp(
                    AppOpsManager.OPSTR_MOCK_LOCATION,
                    android.os.Process.myUid(),
                    context.packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else {
                // in marshmallow this will always return true
                android.provider.Settings.Secure.getString(context.contentResolver, "mock_location") != "0"
            }
        } catch (e: Exception) {
            return isMockLocation
        }

        return isMockLocation
    }

    /**
     * 判断是否为虚拟机
     *
     * @return 是否为虚拟机
     */
    fun isEmulator(): Boolean =
        (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)

    /**
     * 获取系统信息
     */
    fun getSystemProperty(propName: String): String {
        var line: String
        var input: BufferedReader? = null
        try {
            val p: Process = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (e: Exception) {
            return ""
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: Exception) {
                }
            }
        }
        return line
    }

    /**
     * 是否大于Android L
     */
    fun isSupportL(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    /**
     * 是否安装了指定app
     * @param context
     * @param packageName
     * @return
     */
    fun isAppAvailable(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager// 获取packagemanager
        val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == packageName) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    fun getScreenWidth(context: Context) =
        context.resources.displayMetrics.widthPixels
}