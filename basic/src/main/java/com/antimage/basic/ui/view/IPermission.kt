package com.antimage.basic.ui.view

import androidx.annotation.StringRes
import io.reactivex.Observable

interface IPermission {

    fun ensurePermission(permission: String, failMsg: String): Observable<Boolean>?

    fun ensurePermission(permission: String, @StringRes failResId: Int): Observable<Boolean>?
}