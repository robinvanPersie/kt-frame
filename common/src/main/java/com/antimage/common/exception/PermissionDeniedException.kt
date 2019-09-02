package com.antimage.common.exception

/**
 * Created by xuyuming on 2019-08-29
 */
class PermissionDeniedException: Exception {

    var deniedPermissions: Array<String>? = null

    constructor(): super()

    constructor(deniedPermissions: Array<String>): super("permission denied!") {
        this.deniedPermissions = deniedPermissions
    }
}