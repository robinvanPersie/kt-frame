package com.antimage.common.utils.android

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

object FragmentUtils {

//  ============ replace ==============

    fun replace(manager: FragmentManager, containerId: Int, fragment: Fragment) {
        this.replace(manager, containerId, fragment, null)
    }

    fun replace(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?) {
        this.replace(manager, containerId, fragment, tag, false, null, false)
    }

    fun replace(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?, backStack: Boolean, backStackTag: String?) {
        this.replace(manager, containerId, fragment, tag, backStack, backStackTag, false)
    }

    fun replace(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?, backStack: Boolean, backStackTag: String?, ignore: Boolean) {
        if (fragment.isAdded) return
        val transaction = manager.beginTransaction()
        if (backStack) {
            transaction.addToBackStack(backStackTag)
        }
        transaction.replace(containerId, fragment, tag)
        if (ignore) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }


//  ============ add ==========

    fun add(manager: FragmentManager, containerId: Int, fragment: Fragment) {
        this.add(manager, containerId, fragment, null)
    }

    fun add(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?) {
        this.add(manager, containerId, fragment, tag, false, null, false)
    }

    fun add(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?, backStack: Boolean, backStackTag: String?) {
        this.add(manager, containerId, fragment, tag, backStack, backStackTag, false)
    }

    fun add(manager: FragmentManager, containerId: Int, fragment: Fragment, tag: String?, backStack: Boolean, backStackTag: String?, ignore: Boolean) {
        if (fragment.isAdded) return
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (backStack) {
            transaction.addToBackStack(backStackTag)
        }
        transaction.add(containerId, fragment, tag)
        if (ignore) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }

//  ============ show =============

    fun show(manager: FragmentManager, dialogFragment: DialogFragment) {
        dialogFragment.show(manager, null)
    }

    fun show(manager: FragmentManager, dialogFragment: DialogFragment, tag: String?) {
        dialogFragment.show(manager, tag)
    }
}