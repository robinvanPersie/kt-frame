package com.antimage.basic.ui.activity.base

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.antimage.basic.R
import com.antimage.basic.ui.view.FragmentCallback
import com.antimage.common.utils.android.FragmentUtils
import kotlinx.android.synthetic.main.activity_single.*

abstract class SingleFragmentActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackBtn())
        var fragment = supportFragmentManager.findFragmentById(getContainerId())
        if (fragment == null) {
            fragment = createFragment()
            FragmentUtils.replace(supportFragmentManager, getContainerId(), fragment)
        }
    }

    abstract fun createFragment(): Fragment

    abstract fun showBackBtn(): Boolean

    protected fun getContainerId(): Int {
        return R.id.container_id
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(getContainerId())
        if (fragment is FragmentCallback) {
            if (fragment.onFragmentBackPress()) return;
        }
        super.onBackPressed()
    }
}