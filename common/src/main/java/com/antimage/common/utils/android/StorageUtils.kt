package com.antimage.common.utils.android

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.content.ContextCompat
import com.antimage.common.utils.java.CloseUtils
import com.antimage.common.utils.java.ConvertUtils
import com.antimage.common.utils.java.FileUtils

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Version 1.0.0
 *
 *
 * Date: 17/2/6 15:42
 * Author: zhendong.wu@shoufuyou.com
 *
 *
 * Copyright © 2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */

object StorageUtils {

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br></br>false : 不可用
     */
    val isSDCardEnable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /**
     * 获取SD卡路径
     *
     * 先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/
     *
     * @return SD卡路径
     */
    val sdCardPath: String
        get() {
            if (!isSDCardEnable) return "sdcard unable!"
            val cmd = "cat /proc/mounts"
            val run = Runtime.getRuntime()
            var bufferedReader: BufferedReader? = null
            try {
                val p = run.exec(cmd)
                bufferedReader = BufferedReader(InputStreamReader(BufferedInputStream(p.inputStream)))
                var lines = bufferedReader.readLines()
                for (line in lines) {
                    if (line.contains("sdcard") && line.contains(".android_secure")) {
                        val strArray = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (strArray.size >= 5) {
                            return strArray[1].replace("/.android_secure", "") + File.separator
                        }
                    }
                    if (p.waitFor() != 0 && p.exitValue() == 1) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(bufferedReader)
            }
            return Environment.getExternalStorageDirectory().path + File.separator
        }

    val availableInternalMemorySize: String
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = getBlockSize(stat)
            val availableBlocks = getAvailableBlockCount(stat)
            return formatSize(availableBlocks * blockSize)
        }

    val totalInternalMemorySize: String
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = getBlockSize(stat)
            val totalBlocks = getBlockCount(stat)
            return formatSize(totalBlocks * blockSize)
        }

    val availableExternalMemorySize: String?
        get() {
            return if (externalMemoryAvailable()) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = getBlockSize(stat)
                val availableBlocks = getAvailableBlockCount(stat)
                formatSize(availableBlocks * blockSize)
            } else {
                null
            }
        }

    val totalExternalMemorySize: String?
        get() {
            return if (externalMemoryAvailable()) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = getBlockSize(stat)
                val totalBlocks = getBlockCount(stat)
                formatSize(totalBlocks * blockSize)
            } else {
                null
            }
        }

    /**
     * 获取SD卡data路径
     *
     * @return SD卡data路径
     */
    val dataPath: String
        get() = if (!isSDCardEnable) "sdcard unable!" else Environment.getExternalStorageDirectory().path + File.separator + "data" + File.separator

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */
    val freeSpace: String
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {
            if (!isSDCardEnable) return "sdcard unable!"
            val stat = StatFs(sdCardPath)
            val blockSize: Long
            val availableBlocks: Long
            availableBlocks = stat.availableBlocksLong
            blockSize = stat.blockSizeLong
            return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize)
        }

    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */
    val sdCardInfo: String
        get() {
            val sd = SDCardInfo()
            if (!isSDCardEnable) return "sdcard unable!"
            sd.isExist = true
            val sf = StatFs(Environment.getExternalStorageDirectory().path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                sd.totalBlocks = sf.blockCountLong
                sd.blockByteSize = sf.blockSizeLong
                sd.availableBlocks = sf.availableBlocksLong
                sd.availableBytes = sf.availableBytes
                sd.freeBlocks = sf.freeBlocksLong
                sd.freeBytes = sf.freeBytes
                sd.totalBytes = sf.totalBytes
            } else {
                sd.totalBlocks = sf.blockCount.toLong()
                sd.blockByteSize = sf.blockSize.toLong()
                sd.availableBlocks = sf.availableBlocks.toLong()
                sd.availableBytes = (sf.availableBlocks * sf.blockSize).toLong()
                sd.freeBlocks = sf.freeBlocks.toLong()
                sd.freeBytes = (sf.freeBlocks * sf.blockSize).toLong()
                sd.totalBytes = (sf.blockCount * sf.blockSize).toLong()
            }
            return sd.toString()
        }

    /**
     * 获取当前可用的缓存文件目录
     * @param context
     * @param type
     * @return
     */
    fun getProperCacheDir(context: Context, type: String?): File? {
        val files = ContextCompat.getExternalCacheDirs(context)
        var file: File? = null
        if (files != null) {
            for (f in files) {
                if (f != null && isDirectoryWritable(f.absolutePath)) {
                    file = f
                    break
                }
            }
        }

        if (file == null) {
            val f = context.externalCacheDir
            if (f != null && isDirectoryWritable(f.absolutePath)) {
                file = f
            }
        }

        if (file == null) {
            file = context.cacheDir
        }

        if (type != null) {
            file = File(file!!.absolutePath + File.separator + type)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
        return file
    }

    fun getProperExternalCacheDir(context: Context, type: String?): File? {
        val files = ContextCompat.getExternalCacheDirs(context)
        var file: File? = null
        if (files != null) {
            for (f in files) {
                if (f != null && f.canWrite()) {
                    file = f
                    break
                }
            }
        }

        if (file == null) {
            val f = context.externalCacheDir
            if (f != null && f.canWrite()) {
                file = f
            }
        }


        if (file != null && type != null) {
            file = File(file.absolutePath + File.separator + type)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
        return file
    }

    /**
     * 文件夹是否可写
     * @param directory
     * @return
     */
    fun isDirectoryWritable(directory: String): Boolean {
        val file = File(directory)
        if (file.exists() && !file.isDirectory) { // file is file, not folder
            return false
        }
        if (!file.exists()) {
            file.mkdirs()
        }
        if (file.isDirectory) {
            try {
                val temp = File(file.absolutePath + File.separator + "test_temp.txt")
                if (temp.exists()) {
                    temp.delete()
                }
                temp.createNewFile()
                temp.delete()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return false
    }

    /**
     * 清除内部缓存
     *
     * /data/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalCache(context: Context): Boolean {
        return FileUtils.deleteFilesInDir(context.cacheDir)
    }

    /**
     * 清除内部文件
     *
     * /data/data/com.xxx.xxx/files
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalFiles(context: Context): Boolean {
        return FileUtils.deleteFilesInDir(context.filesDir)
    }

    /**
     * 清除内部数据库
     *
     * /data/data/com.xxx.xxx/databases
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalDbs(context: Context): Boolean {
        return FileUtils.deleteFilesInDir(context.filesDir.parent + File.separator + "databases")
    }

    /**
     * 根据名称清除数据库
     *
     * /data/data/com.xxx.xxx/databases/dbName
     *
     * @param dbName  数据库名称
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalDbByName(context: Context, dbName: String): Boolean {
        return context.deleteDatabase(dbName)
    }

    /**
     * 清除内部SP
     *
     * /data/data/com.xxx.xxx/shared_prefs
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalSP(context: Context): Boolean {
        return FileUtils.deleteFilesInDir(context.filesDir.parent + File.separator + "shared_prefs")
    }

    /**
     * 清除外部缓存
     *
     * /storage/emulated/0/android/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanExternalCache(context: Context): Boolean {
        return isSDCardEnable && FileUtils.deleteFilesInDir(context.externalCacheDir)
    }


    fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun getBlockSize(statFs: StatFs): Long {
        return if (Build.VERSION.SDK_INT >= 18) {
            statFs.blockSizeLong
        } else {
            statFs.blockSize.toLong()
        }
    }

    private fun getBlockCount(statFs: StatFs): Long {
        return if (Build.VERSION.SDK_INT >= 18) {
            statFs.blockCountLong
        } else {
            statFs.blockCount.toLong()
        }
    }

    private fun getAvailableBlockCount(statFs: StatFs): Long {
        return if (Build.VERSION.SDK_INT >= 18) {
            statFs.availableBlocksLong
        } else {
            statFs.availableBlocks.toLong()
        }
    }


    private fun formatSize(size: Long): String {
        var suffix = "B"

        var s = size * 1f

        if (s >= 1024f) {
            suffix = "K"
            s /= 1024f
        }
        if (s >= 1024f) {
            suffix = "M"
            s /= 1024f
        }
        if (s >= 1024f) {
            suffix = "G"
            s /= 1024f
        }
        return String.format("%.2f%s", s, suffix)
    }

    class SDCardInfo {
        internal var isExist: Boolean = false
        internal var totalBlocks: Long = 0
        internal var freeBlocks: Long = 0
        internal var availableBlocks: Long = 0
        internal var blockByteSize: Long = 0
        internal var totalBytes: Long = 0
        internal var freeBytes: Long = 0
        internal var availableBytes: Long = 0

        override fun toString(): String {
            return "isExist=" + isExist +
                    "\ntotalBlocks=" + totalBlocks +
                    "\nfreeBlocks=" + freeBlocks +
                    "\navailableBlocks=" + availableBlocks +
                    "\nblockByteSize=" + blockByteSize +
                    "\ntotalBytes=" + totalBytes +
                    "\nfreeBytes=" + freeBytes +
                    "\navailableBytes=" + availableBytes
        }
    }
}
