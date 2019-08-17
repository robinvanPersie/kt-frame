package app.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.antimage.basic.ui.activity.base.BaseActivity
import com.antimage.common.utils.android.FragmentUtils
import com.antimage.loginmodule.R
import com.antimage.loginmodule.ui.LoginDialogFragment

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = FrameLayout(this)
        layout.id = R.id.container_id
        val params = ViewGroup.LayoutParams(-1, -1)
        setContentView(layout, params)

        FragmentUtils.show(supportFragmentManager, LoginDialogFragment.newInstance())
    }
}