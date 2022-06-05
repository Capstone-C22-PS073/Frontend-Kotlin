package com.capstonec22ps073.toursight.util

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.capstonec22ps073.toursight.R

class CustomDialog(
    private val mActivity: Activity,
    private val isError: Boolean,
    private val title: Int,
    private val message: Int
) : AlertDialog.Builder(mActivity) {
    private lateinit var isDialog: AlertDialog

    fun startDialogError() {
        val mInflater = mActivity.layoutInflater
        val dialogView = mInflater.inflate(R.layout.custom_dialog_item, null)

        val btn = dialogView.findViewById<Button>(R.id.btn_close)
        val lottie = dialogView.findViewById<LottieAnimationView>(R.id.lottie_empty_content)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        val message = dialogView.findViewById<TextView>(R.id.tv_message)

        if (isError) {
            btn.setBackgroundColor(context.resources.getColor(R.color.error_background))
            if (this.title == R.string.no_internet) {
                lottie.setAnimation(R.raw.lottie_no_internet)
            } else {
                lottie.setAnimation(R.raw.lottie_error_occurred)
            }
        } else {
            btn.setBackgroundColor(context.resources.getColor(R.color.success_background))
            lottie.setAnimation(R.raw.lottie_success)
        }

        title.setText(this.title)
        message.setText(this.message)

        btn.setOnClickListener {
            dismissDialogError()
        }

        val mBuilder = AlertDialog.Builder(mActivity)
        mBuilder.setView(dialogView)
        mBuilder.setCancelable(false)

        isDialog = mBuilder.create()

        isDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        isDialog.window?.setGravity(Gravity.CENTER)

        isDialog.show()
    }

    private fun dismissDialogError() {
        isDialog.dismiss()
    }
}