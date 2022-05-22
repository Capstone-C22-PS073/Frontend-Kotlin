package com.capstonec22ps073.toursight.view

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.capstonec22ps073.toursight.databinding.ActivityPreviewCaptureBinding
import com.capstonec22ps073.toursight.util.rotateBitmap
import com.capstonec22ps073.toursight.util.uriToFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class PreviewCaptureActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityPreviewCaptureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val status = intent.getStringExtra("status")
        if (status == "gallery") {
            setImageFromGallery()
        } else {
            setImageFromCameraX()
        }

        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun setImageFromCameraX() {
        val myFile = intent.getSerializableExtra("picture") as File
        val isBackCamera = intent.getBooleanExtra("isBackCamera", true)

        val result = rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )
        showLoading(true)
        getFile = convertBitmapToFile(result)
        showLoading(false)
        binding.previewImageView.setImageBitmap(result)
    }

    private fun setImageFromGallery() {
        val selectedImg: Uri = intent.getParcelableExtra<Uri>("picture") as Uri
        val myFile = uriToFile(selectedImg, this)
        getFile = myFile
        binding.previewImageView.setImageURI(selectedImg)
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        val wrapper = ContextWrapper(this)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)
        stream.flush()
        stream.close()
        return file
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }
}