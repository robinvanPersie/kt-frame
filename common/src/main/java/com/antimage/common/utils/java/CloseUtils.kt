package com.antimage.common.utils.java

import java.io.Closeable
import java.io.IOException

/**
 * Created by xuyuming on 2019-08-28
 */
object CloseUtils {

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    fun closeIO(vararg closeables: Closeable?) {
        if (closeables == null) return
        for (closeable in closeables) {
            try {
                closeable?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeable
     */
    fun closeIOQuietly(vararg closeables: Closeable?) {
        if (closeables == null) return
        for (closeable in closeables) {
            try {
                closeable?.close()
            } catch (ignored: IOException) {
            }
        }
    }
}