package com.antimage.loginmodule.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.antimage.basic.router.Router
import com.antimage.basic.ui.fragment.base.LifeCycleDialogFragment
import com.antimage.loginmodule.presenter.LoginPresenter
import com.antimage.loginmodule.R
import com.antimage.loginmodule.view.LoginView
import kotlinx.android.synthetic.main.fragment_login.*

@Route(path = Router.Login.LOGIN_DIALOG_FRAGMENT)
class LoginDialogFragment: LifeCycleDialogFragment<LoginView, LoginPresenter>(), LoginView {

    companion object {

        fun newInstance(): LoginDialogFragment {
            return LoginDialogFragment()
        }
    }

    override fun createPresenter(): LoginPresenter {
        return LoginPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_account.hint = "input your account"
        et_verification_code.hint = "length less than 4"
        btn_confirm.setOnClickListener {
            println("btn clicked")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog.window.statusBarColor = resources.getColor(android.R.color.white)
        }
        //使得dialog全屏
//        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}