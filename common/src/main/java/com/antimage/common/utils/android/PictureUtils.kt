package com.antimage.common.utils.android

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.antimage.common.exception.PermissionDeniedException
import com.antimage.common.utils.java.CloseUtils
import java.io.*

/**
 * Created by xuyuming on 2019-08-28
 */
object PictureUtils {

    private const val AUTHOR = ".FileProvider"
    private const val TEMP_DIRECTORY = "temp"
    private const val CAMERA_TEMP_FILE = "temp_cameraNXX.png"
    private const val CROP_TEMP_FILE = "temp_cropNXX.png"
    private const val CROP_TEMP_SOURCE_FILE = "temp_source_cropNXX.png"
    private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    private const val COLOR_DRAWABLE_DIMENSION = 2

    /**
     * 选择图片
     */
    fun choosePicture(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    /**
     * 照相
     *
     * @param context 上下文
     */
    fun takeCamera(context: Context): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var cameraTempFile = File(StorageUtils.getProperCacheDir(context, TEMP_DIRECTORY), CAMERA_TEMP_FILE)
        if (!cameraTempFile.parentFile.exists()) {
            cameraTempFile.parentFile.mkdirs()
        }
        if (cameraTempFile.exists()) {
            cameraTempFile.delete()
        }
        val author = context.packageName + AUTHOR
        var photoUri: Uri? = null
        try {
            photoUri = FileProvider.getUriForFile(context, author, cameraTempFile)
        } catch (e: Exception) {
            cameraTempFile =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CAMERA_TEMP_FILE)
            try {
                photoUri = Uri.fromFile(cameraTempFile)
            } catch (e1: Exception) {
                Log.e(javaClass.simpleName, e1.toString())
            }
            Log.e(javaClass.simpleName, e.toString())
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    photoUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
        return intent
    }

    fun cropPicture(
        context: Context,
        source: Uri,
        aspectX: Int,
        aspectY: Int,
        outX: Int,
        outY: Int,
        scaled: Boolean,
        returnData: Boolean,
        circleCrop: Boolean
    ): Intent {
        var source = source
        val author = context.packageName + AUTHOR
        if (ContentResolver.SCHEME_FILE == source.scheme) {
            //android N 不允许向外部应用发送file://的uri
            val file = File(source.path!!)
            if (file.exists()) {
                try {
                    source = FileProvider.getUriForFile(context, author, file)
                } catch (e: Exception) {
                    //如果返回的是未配置的路径则使用原始的uri
                    Log.e(javaClass.simpleName, e.toString())
                }

            }
        }
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(source, "image/*")
        intent.putExtra("crop", true)
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outX)
        intent.putExtra("outputY", outY)
        intent.putExtra("scale", scaled)
        intent.putExtra("return-data", returnData)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG)
        val cropTempFile = File(StorageUtils.getProperCacheDir(context, TEMP_DIRECTORY), CROP_TEMP_FILE)
        if (!cropTempFile.parentFile.exists()) {
            cropTempFile.parentFile.mkdirs()
        }
        if (cropTempFile.exists()) {
            cropTempFile.delete()
        }
        val outPutUri = FileProvider.getUriForFile(context, author, cropTempFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        //        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        try {
            val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    outPutUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                context.grantUriPermission(
                    packageName,
                    source,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        } catch (e: SecurityException) {
            //maybe no permission to grant origin source uri
            //if picture is large then this operation will block main thread long time
            var ins: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                ins = context.contentResolver.openInputStream(source)
                val cropTempSourceFile =
                    File(StorageUtils.getProperCacheDir(context, TEMP_DIRECTORY), CROP_TEMP_SOURCE_FILE)
                if (!cropTempSourceFile.parentFile.exists()) {
                    cropTempSourceFile.parentFile.mkdirs()
                }
                outputStream = FileOutputStream(cropTempSourceFile)
                val buffer = ByteArray(512)
                var len: Int
                ins.use {
                    inputStream ->
                    outputStream.use { it ->
                        while (inputStream.read(buffer).also { len = it } != -1) {
                            it.write(len)
                        }
                    }
                }
                outputStream.flush()
                outputStream.close()
                val newSource = FileProvider.getUriForFile(context, author, cropTempSourceFile)
                intent.setDataAndType(newSource, "image/*")
            } catch (e1: Exception) {
                Log.e(javaClass.simpleName, e1.toString())
            } finally {
                CloseUtils.closeIOQuietly(ins, outputStream)
            }
        } catch (e: Exception) {
            Log.d(javaClass.simpleName, e.toString())
        }

        //        }
        intent.putExtra("circleCrop", circleCrop)
        return intent
    }


//    @Throws(PermissionDeniedException::class)
    fun parsePictureResult(context: Context, data: Intent?): Uri? {
        if (data != null && data.data != null) {
            //if from gallery and the scheme is file need hold the read external storage permission
            if (ContentResolver.SCHEME_FILE == data.data!!.scheme) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    //no permission need request the permission
                    val permissionDeniedException = PermissionDeniedException()
                    permissionDeniedException.deniedPermissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE")
                    throw permissionDeniedException
                }
            }
            return data.data
        }
        return null
    }

//    @Throws(PermissionDeniedException::class)
    fun parseCameraResult(context: Context, data: Intent?): Uri? {
        if (data != null && data.data != null) {
            //if from gallery and the scheme is file need hold the read external storage permission
            if (ContentResolver.SCHEME_FILE == data.data!!.scheme) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    //no permission need request the permission
                    val permissionDeniedException = PermissionDeniedException()
                    permissionDeniedException.deniedPermissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE")
                    throw permissionDeniedException
                }
            }
            return data.data
        }
        val file = File(StorageUtils.getProperCacheDir(context, TEMP_DIRECTORY), CAMERA_TEMP_FILE)
        if (file.exists()) {
            return Uri.Builder().scheme(ContentResolver.SCHEME_FILE).path(file.absolutePath).build()
        } else {
            val cameraTempFile =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CAMERA_TEMP_FILE)
            if (cameraTempFile.exists()) {
                return Uri.Builder().scheme(ContentResolver.SCHEME_FILE).path(cameraTempFile.absolutePath).build()
            }
        }
        return null
    }


//    @Throws(PermissionDeniedException::class)
    fun parseCropResult(context: Context, data: Intent?): Uri? {
        if (data != null && data.data != null) {
            //if the uri come from the gallery and the scheme is file ,now need hold the read external storage permission
            if (ContentResolver.SCHEME_FILE == data.data!!.scheme) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    //no permission granted ,require request the permission
                    val permissionDeniedException = PermissionDeniedException()
                    permissionDeniedException.deniedPermissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE")
                    throw permissionDeniedException
                }
            }
            return data.data
        }
        val file = File(StorageUtils.getProperCacheDir(context, TEMP_DIRECTORY), CROP_TEMP_FILE)
        return if (file.exists()) {
            Uri.Builder().scheme(ContentResolver.SCHEME_FILE).path(file.absolutePath).build()
        } else null
    }

    fun compressToFile(context: Context, uri: Uri, maxWidth: Float, maxHeight: Float, outFilePath: String): File? {
        val bitmap = getScaledBitmap(context, uri, maxWidth, maxHeight)
        try {
            val fileOutputStream = FileOutputStream(outFilePath)
            val success = bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            if (success) {
                return File(outFilePath)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return null
    }


    fun compressToBitmap(context: Context, uri: Uri, maxWidth: Float, maxHeight: Float): Bitmap? {
        return getScaledBitmap(context, uri, -1f, -1f)
    }


    private fun getScaledBitmap(context: Context, imageUri: Uri, maxWidth: Float, maxHeight: Float): Bitmap? {
        try {
            val contentResolver = context.contentResolver
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth
            var imgRatio = actualWidth.toFloat() / actualHeight
            val maxRatio = maxWidth / maxHeight
            //width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

            //setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = computeSampleSize(options, -1, actualWidth * actualHeight)
            //inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

            //this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)
            var scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, 0f, 0f)

            val canvas = Canvas(scaledBitmap)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(bitmap!!, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))

            //check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(getPath(context, imageUri))
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0,
                    scaledBitmap.width, scaledBitmap.height,
                    matrix, true
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(javaClass.simpleName, "image get exif failure $e")
            }

            return scaledBitmap
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e(javaClass.simpleName, e.toString())
        }
        return null
    }


    private fun computeSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels)
        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }
        return roundedSize
    }

    private fun computeInitialSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()
        val lowerBound = if (maxNumOfPixels == -1) 1 else Math.ceil(Math.sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == -1) 128 else Math.min(
            Math.floor(w / minSideLength),
            Math.floor(h / minSideLength)
        ).toInt()
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound
        }
        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }


    /**
     * 通过Drawable获取Bitmap
     *
     * @param drawable 源
     * @return bitmap
     */
    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            val bitmap: Bitmap

            if (drawable is ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    fun getPath(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(
                    context, contentUri, selection,
                    selectionArgs
                )
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }


    /**
     * 默认压缩不能超过2M
     * @param image
     * @return
     */
    fun compressImage(image: Bitmap): Bitmap? {
        return compressImage(image, 1024)
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    fun compressImage(image: Bitmap, maxSize: Int): Bitmap? {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90

        while (baos.toByteArray().size / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }


    /**
     * 把Byte[]转化为Bitmap
     * @param bytes
     * @return
     */
    internal fun Bytes2Bimap(bytes: ByteArray): Bitmap? {
        return if (bytes.size != 0) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }


    /**
     * 把Bitmap转Byte
     */
    fun Bitmap2Bytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }


    /**
     * 把字节数组保存为一个文件
     */
    fun getFileFromBytes(b: ByteArray, fileName: String): File? {
        var stream: BufferedOutputStream? = null
        var file: File? = null
        try {
            file = File(fileName)
            val fstream = FileOutputStream(file)
            stream = BufferedOutputStream(fstream)
            stream.write(b)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }
        return file
    }

    /**
     *
     * @param bytes
     * @param maxSize 最大大小 kb
     * @return
     */
    fun getScaleByteByBytes(bytes: ByteArray, maxSize: Int): ByteArray? {
        val image: Bitmap
        if (bytes.size != 0) {
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            return null
        }

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90

        while (baos.toByteArray().size / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }

        return baos.toByteArray()
    }


    /**
     *
     * @param bytes
     * @param maxSize 最大大小 kb
     * @return
     */
    fun getScaleFileByBytes(bytes: ByteArray, maxSize: Int, fileName: String): File? {
        val image: Bitmap
        if (bytes.isNotEmpty()) {
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            return null
        }

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90

        while (baos.toByteArray().size / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }

        //转化为file
        var stream: BufferedOutputStream? = null
        var file: File? = null
        try {
            file = File(fileName)
            val fstream = FileOutputStream(file)
            stream = BufferedOutputStream(fstream)
            stream.write(baos.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }
        return file
    }
}