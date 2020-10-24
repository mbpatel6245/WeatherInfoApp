package com.mbpatel.weatherinfo.utils.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity



/*
*  Ref : https://github.com/florent37/RuntimePermission
* */

fun Fragment.askRuntimePermission(vararg permissions: String, acceptedblock:
    (PermissionResult) -> Unit): KotlinRuntimePermission {
    return KotlinRuntimePermission(
        RuntimePermission.askPermission(activity)
            .request(permissions.toList())
            .onAccepted(acceptedblock)
    )
}

fun FragmentActivity.askRuntimePermission(vararg permissions: String, acceptedblock:
    (PermissionResult) -> Unit): KotlinRuntimePermission {
    return KotlinRuntimePermission(
        RuntimePermission.askPermission(this)
            .request(permissions.toList())
            .onAccepted(acceptedblock)
    )
}

class KotlinRuntimePermission(var runtimePermission: RuntimePermission) {

    init {
        runtimePermission.ask()
    }

    fun onDeclined(block: ((PermissionResult) -> Unit)) : KotlinRuntimePermission {
        runtimePermission.onResponse{
            if(it.hasDenied() || it.hasForeverDenied()){
                block(it)
            }
        }
        return this
    }
}