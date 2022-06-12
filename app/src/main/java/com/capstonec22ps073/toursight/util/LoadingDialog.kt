package com.capstonec22ps073.toursight.util

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.view.WindowManager
import com.capstonec22ps073.toursight.R

class LoadingDialog(private val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog

    fun startDialogLoading() {
        val mInflater = mActivity.layoutInflater
        val dialogView = mInflater.inflate(R.layout.loading_dialog_item, null)

        val mBuilder = AlertDialog.Builder(mActivity)
        mBuilder.setView(dialogView)
        mBuilder.setCancelable(false)
        isDialog = mBuilder.create()

        isDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        isDialog.window?.setGravity(Gravity.CENTER)

        isDialog.show()
    }

    fun dismissDialogLoading() {
        isDialog.dismiss()
    }
}