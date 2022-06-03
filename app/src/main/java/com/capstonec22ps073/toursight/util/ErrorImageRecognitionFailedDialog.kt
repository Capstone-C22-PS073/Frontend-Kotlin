package com.capstonec22ps073.toursight.util

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import com.capstonec22ps073.toursight.R

class ErrorImageRecognitionFailedDialog(private val mActivity: Activity): AlertDialog.Builder(mActivity) {
    private lateinit var isDialog: AlertDialog

    fun startDialogError() {
        val mInflater = mActivity.layoutInflater
        val dialogView = mInflater.inflate(R.layout.error_dialog_item, null)
        val btn = dialogView.findViewById<Button>(R.id.btn_close)
        btn.setOnClickListener {
            dismissDialogError()
        }

        val mBuilder = AlertDialog.Builder(mActivity)
        mBuilder.setView(dialogView)
        mBuilder.setCancelable(false)

        isDialog = mBuilder.create()

        isDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        isDialog.window?.setGravity(Gravity.CENTER)

        isDialog.show()
    }

    private fun dismissDialogError() {
        isDialog.dismiss()
    }
}