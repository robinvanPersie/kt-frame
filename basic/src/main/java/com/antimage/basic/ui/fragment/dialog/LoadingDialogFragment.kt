package com.antimage.basic.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StringRes
import com.antimage.basic.R
import com.antimage.basic.ui.fragment.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_loading.*

class LoadingDialogFragment : BaseDialogFragment() {

    companion object {

        fun newInstance(@StringRes resId: Int): LoadingDialogFragment {
            val fragment = LoadingDialogFragment()
            fragment.resId = resId
            return fragment
        }

        fun newInstance(msg: String): LoadingDialogFragment {
            val fragment = LoadingDialogFragment()
            fragment.msg = msg
            return fragment
        }
    }

    private var resId: Int = 0
    private var msg: String? = null


    fun updateMessage(msg: String) {
        text_msg.text = msg
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_fragment_loading, container, false)
        if (resId == 0) {
            text_msg.text = msg
        } else {
            text_msg.setText(resId)
        }
        return view
    }
}