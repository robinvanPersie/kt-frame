package com.antimage.common.utils.java

import java.io.*
import java.nio.channels.FileChannel
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by xuyuming on 2019-08-29
 */
object FileUtils {

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String): File? {
        return if (filePath.trim().isEmpty()) null else File(filePath)
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(filePath: String): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return `true`: 重命名成功<br></br>`false`: 重命名失败
     */
    fun rename(filePath: String, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return `true`: 重命名成功<br></br>`false`: 重命名失败
     */
    fun rename(file: File?, newName: String): Boolean {
        // 文件为空返回false
        if (file == null) return false
        // 文件不存在返回false
        if (!file.exists()) return false
        // 新的文件名为空返回false
        if (newName.trim().isEmpty()) return false
        // 如果文件名没有改变返回true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // 如果重命名的文件已存在返回false
        return !newFile.exists() && file.renameTo(newFile)
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isDir(dirPath: String): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isDir(file: File?): Boolean {
        return isFileExists(file) && file!!.isDirectory
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isFile(filePath: String): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isFile(file: File?): Boolean {
        return isFileExists(file) && file!!.isFile
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(dirPath: String): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(filePath: String): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return `true`: 创建成功<br></br>`false`: 创建失败
     */
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return `true`: 创建成功<br></br>`false`: 创建失败
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile && !file.delete()) return false
        // 创建目录失败返回false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param isMove      是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    private fun copyOrMoveDir(srcDirPath: String, destDirPath: String, isMove: Boolean): Boolean {
        return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), isMove)
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isMove  是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    private fun copyOrMoveDir(srcDir: File?, destDir: File?, isMove: Boolean): Boolean {
        if (srcDir == null || destDir == null) return false
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        for (file in files) {
            val oneDestFile = File(destPath + file.name)
            if (file.isFile) {
                // 如果操作失败返回false
                if (!copyOrMoveFile(file, oneDestFile, isMove)) return false
            } else if (file.isDirectory) {
                // 如果操作失败返回false
                if (!copyOrMoveDir(file, oneDestFile, isMove)) return false
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param isMove       是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    private fun copyOrMoveFile(srcFilePath: String, destFilePath: String, isMove: Boolean): Boolean {
        return copyOrMoveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), isMove)
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param isMove   是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    private fun copyOrMoveFile(srcFile: File?, destFile: File?, isMove: Boolean): Boolean {
        if (srcFile == null || destFile == null) return false
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile) return false
        // 目标文件存在且是文件则返回false
        if (destFile.exists() && destFile.isFile) return false
        // 目标目录不存在返回false
        if (!createOrExistsDir(destFile.parentFile)) return false
        return try {
            writeFileFromIS(destFile, FileInputStream(srcFile), false) && !(isMove && !deleteFile(srcFile))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }

    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyDir(srcDirPath: String, destDirPath: String): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 复制目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyDir(srcDir: File?, destDir: File?): Boolean {
        return copyOrMoveDir(srcDir, destDir, false)
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyFile(srcFilePath: String, destFilePath: String): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath))
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyFile(srcFile: File?, destFile: File?): Boolean {
        return copyOrMoveFile(srcFile, destFile, false)
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveDir(srcDirPath: String, destDirPath: String): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveDir(srcDir: File?, destDir: File?): Boolean {
        return copyOrMoveDir(srcDir, destDir, true)
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveFile(srcFilePath: String, destFilePath: String): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath))
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveFile(srcFile: File?, destFile: File?): Boolean {
        return copyOrMoveFile(srcFile, destFile, true)
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dirPath: String): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // 目录不存在返回true
        if (!dir.exists()) return true
        // 不是目录返回false
        if (!dir.isDirectory) return false
        // 现在文件存在且是文件夹
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!deleteFile(file)) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFile(srcFilePath: String): Boolean {
        return deleteFile(getFileByPath(srcFilePath))
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFilesInDir(dirPath: String): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        if (dir == null) return false
        // 目录不存在返回true
        if (!dir.exists()) return true
        // 不是目录返回false
        if (!dir.isDirectory) return false
        // 现在文件存在且是文件夹
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!deleteFile(file)) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return true
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String, isRecursive: Boolean): List<File>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File>? {
        if (!isDir(dir)) return null
        if (isRecursive) return listFilesInDir(dir)
        val list = ArrayList<File>()
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
//            Collections.addAll(list, *files)
            list.addAll(files)

        }
        return list
    }

    /**
     * 获取目录下所有文件包括子目录
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String): List<File>? {
        return listFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 获取目录下所有文件包括子目录
     *
     * @param dir 目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?): List<File>? {
        if (!isDir(dir)) return null
        val list = mutableListOf<File>()
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                list.add(file)
                if (file.isDirectory) {
                    list.addAll(listFilesInDir(file)!!.toTypedArray())
                }
            }
        }
        return list
    }

    /**
     * 获取目录下所有后缀名为suffix的文件
     *
     * 大小写忽略
     *
     * @param dirPath     目录路径
     * @param suffix      后缀名
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String, suffix: String, isRecursive: Boolean): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), suffix, isRecursive)
    }

    /**
     * 获取目录下所有后缀名为suffix的文件
     *
     * 大小写忽略
     *
     * @param dir         目录
     * @param suffix      后缀名
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, suffix: String, isRecursive: Boolean): List<File>? {
        if (isRecursive) return listFilesInDirWithFilter(dir, suffix)
        if (dir == null || !isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                    list.add(file)
                }
            }
        }
        return list
    }

    /**
     * 获取目录下所有后缀名为suffix的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dirPath 目录路径
     * @param suffix  后缀名
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String, suffix: String): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), suffix)
    }

    /**
     * 获取目录下所有后缀名为suffix的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dir    目录
     * @param suffix 后缀名
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, suffix: String): List<File>? {
        if (dir == null || !isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                    list.add(file)
                }
                if (file.isDirectory) {
                    list.addAll(listFilesInDirWithFilter(file, suffix)!!.toTypedArray())
                }
            }
        }
        return list
    }

    /**
     * 获取目录下所有符合filter的文件
     *
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String, filter: FilenameFilter, isRecursive: Boolean): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * 获取目录下所有符合filter的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, filter: FilenameFilter, isRecursive: Boolean): List<File>? {
        if (isRecursive) return listFilesInDirWithFilter(dir, filter)
        if (dir == null || !isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file.parentFile, file.name)) {
                    list.add(file)
                }
            }
        }
        return list
    }

    /**
     * 获取目录下所有符合filter的文件包括子目录
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String, filter: FilenameFilter): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 获取目录下所有符合filter的文件包括子目录
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, filter: FilenameFilter): List<File>? {
        if (dir == null || !isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file.parentFile, file.name)) {
                    list.add(file)
                }
                if (file.isDirectory) {
                    list.addAll(listFilesInDirWithFilter(file, filter)!!.toTypedArray())
                }
            }
        }
        return list
    }

    /**
     * 获取目录下指定文件名的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dirPath  目录路径
     * @param fileName 文件名
     * @return 文件链表
     */
    fun searchFileInDir(dirPath: String, fileName: String): List<File>? {
        return searchFileInDir(getFileByPath(dirPath), fileName)
    }

    /**
     * 获取目录下指定文件名的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 文件链表
     */
    fun searchFileInDir(dir: File?, fileName: String): List<File>? {
        if (dir == null || !isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.name.toUpperCase() == fileName.toUpperCase()) {
                    list.add(file)
                }
                if (file.isDirectory) {
                    list.addAll(searchFileInDir(file, fileName)!!.toTypedArray())
                }
            }
        }
        return list
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromIS(filePath: String, `is`: InputStream, append: Boolean): Boolean {
        return writeFileFromIS(getFileByPath(filePath), `is`, append)
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromIS(file: File?, ins: InputStream?, append: Boolean): Boolean {
        if (file == null || ins == null) return false
        if (!createOrExistsFile(file)) return false
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append))
            val data = ByteArray(1024)
            var len: Int
            ins.use { input ->
                os.use { it ->
                    while (input.read(data).also { len = it } != -1) {
                        it.write(data, 0, len)
                    }
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            CloseUtils.closeIO(ins, os)
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromString(filePath: String, content: String, append: Boolean): Boolean {
        return writeFileFromString(getFileByPath(filePath), content, append)
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromString(file: File?, content: String?, append: Boolean): Boolean {
        if (file == null || content == null) return false
        if (!createOrExistsFile(file)) return false
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(FileWriter(file, append))
            bw.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            CloseUtils.closeIO(bw)
        }
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    fun readFile2List(filePath: String, charsetName: String): List<String>? {
        return readFile2List(getFileByPath(filePath), charsetName)
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    fun readFile2List(file: File?, charsetName: String): List<String>? {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param filePath    文件路径
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含制定行的list
     */
    fun readFile2List(filePath: String, st: Int, end: Int, charsetName: String): List<String>? {
        return readFile2List(getFileByPath(filePath), st, end, charsetName)
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param file        文件
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含从start行到end行的list
     */
    fun readFile2List(file: File?, st: Int, end: Int, charsetName: String): List<String>? {
        if (file == null) return null
        if (st > end) return null
        var reader: BufferedReader? = null
        try {
            var curLine = 1
            val list = ArrayList<String>()
            reader = if (charsetName.trim().isEmpty()) {
                BufferedReader(FileReader(file))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            reader.useLines {
                it.forEach {line ->
                    if (curLine > end) return@forEach
                    if (st <= curLine) list.add(line)
                    ++curLine
                }
            }
            return list
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(reader)
        }
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    fun readFile2String(filePath: String, charsetName: String): String? {
        return readFile2String(getFileByPath(filePath), charsetName)
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    fun readFile2String(file: File?, charsetName: String): String? {
        if (file == null) return null
        var reader: BufferedReader? = null
        try {
            val sb = StringBuilder()
            if (charsetName.trim().isEmpty()) {
                reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                reader = BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
//            var line: String
            reader.forEachLine { line ->
                sb.append(line).append("\r\n")
            }
            // 要去除最后的换行符
            return sb.delete(sb.length - 2, sb.length).toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(reader)
        }
    }

    /**
     * 读取文件到字符数组中
     *
     * @param filePath 文件路径
     * @return 字符数组
     */
    fun readFile2Bytes(filePath: String): ByteArray? {
        return readFile2Bytes(getFileByPath(filePath))
    }

    /**
     * 读取文件到字符数组中
     *
     * @param file 文件
     * @return 字符数组
     */
    fun readFile2Bytes(file: File?): ByteArray? {
        if (file == null) return null

        return try {
            var size = getFileLength(file)
            if (size >= 1024 * 1024 * 1024 * 2) {
                ConvertUtils.inputStream2Bytes(FileInputStream(file))
            } else {
                file.readBytes()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }

    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(filePath: String): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    fun getFileCharsetSimple(filePath: String): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    fun getFileCharsetSimple(file: File?): String {
        var p = 0
        var ins: InputStream? = null
        try {
            ins = BufferedInputStream(FileInputStream(file!!))
            p = (ins.read() shl 8) + ins.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(ins)
        }
        return when (p) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 获取目录大小
     *
     * @param dirPath 目录路径
     * @return 文件大小
     */
    fun getDirSize(dirPath: String): String {
        return getDirSize(getFileByPath(dirPath))
    }

    /**
     * 获取目录大小
     *
     * @param dir 目录
     * @return 文件大小
     */
    fun getDirSize(dir: File?): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else ConvertUtils.byte2FitMemorySize(len)
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileSize(filePath: String): String {
        return getFileSize(getFileByPath(filePath))
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     */
    fun getFileSize(file: File?): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else ConvertUtils.byte2FitMemorySize(len)
    }

    /**
     * 获取目录长度
     *
     * @param dirPath 目录路径
     * @return 文件大小
     */
    fun getDirLength(dirPath: String): Long {
        return getDirLength(getFileByPath(dirPath))
    }

    /**
     * 获取目录长度
     *
     * @param dir 目录
     * @return 文件大小
     */
    fun getDirLength(dir: File?): Long {
        if (!isDir(dir)) return -1
        var len: Long = 0
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isDirectory) {
                    len += getDirLength(file)
                } else {
                    len += file.length()
                }
            }
        }
        return len
    }

    /**
     * 获取文件长度
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileLength(filePath: String): Long {
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件大小
     */
    fun getFileLength(file: File?): Long {
        var size = -1L
        if (file == null || !isFile(file)) return size
        var fc: FileChannel? = null
        try {
            fc = file.inputStream().channel
            size = fc.size()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fc?.close()
        }
        return size
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    fun getFileMD5ToString(filePath: String): String {
        val file = if (filePath.trim().isEmpty()) null else File(filePath)
        return getFileMD5ToString(file)
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    fun getFileMD5(filePath: String): ByteArray? {
        val file = if (filePath.trim().isEmpty()) null else File(filePath)
        return getFileMD5(file)
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    fun getFileMD5ToString(file: File?): String {
        return ConvertUtils.bytes2HexString(getFileMD5(file))
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (dis.read(buffer) > 0);
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(dis)
        }
        return null
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param file 文件
     * @return filePath最长目录
     */
    fun getDirName(file: File?): String? {
        return if (file == null) null else getDirName(file.path)
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath最长目录
     */
    fun getDirName(filePath: String): String {
        if (filePath.trim().isEmpty()) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    fun getFileName(file: File?): String? {
        return if (file == null) null else getFileName(file.path)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    fun getFileName(filePath: String): String {
        if (filePath.trim().isEmpty()) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) null else getFileNameNoExtension(file.path)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(filePath: String): String {
        if (filePath.trim().isEmpty()) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) null else getFileExtension(file.path)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    fun getFileExtension(filePath: String): String {
        if (filePath.trim().isEmpty()) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }
}