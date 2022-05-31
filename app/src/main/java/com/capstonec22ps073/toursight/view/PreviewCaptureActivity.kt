package com.capstonec22ps073.toursight.view

import android.R.attr.bitmap
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.capstonec22ps073.toursight.databinding.ActivityPreviewCaptureBinding
import com.capstonec22ps073.toursight.tflite.Classifier
import com.capstonec22ps073.toursight.util.rotateBitmap
import com.capstonec22ps073.toursight.util.uriToFile
import org.tensorflow.lite.support.model.Model
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class PreviewCaptureActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityPreviewCaptureBinding

    private val inputSize = 128
    private val modelPath = "model.tflite"
    private val labelPath = "label.txt"
    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClassifier()

        val status = intent.getStringExtra("status")
        if (status == "gallery") {
            setImageFromGallery()
        } else {
            setImageFromCameraX()
        }

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnUpload.setOnClickListener {
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val result = classifier.recognizeImage(bitmap)

            Log.d("Hasil Result ", result?.get(0)?.title.toString() + String.format(" Confidence : %.2f",(result?.get(0)?.confidence)))
        }

    }

    private fun initClassifier(){
        classifier = Classifier(assets, modelPath, labelPath, inputSize)
    }

    private fun setImageFromCameraX() {
        val myFile = intent.getSerializableExtra("picture") as File
        val isBackCamera = intent.getBooleanExtra("isBackCamera", true)

        val result: Bitmap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val ie = ExifInterface(myFile)

            val orientation = ie.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            result = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                    BitmapFactory.decodeFile(myFile.path),
                    90
                )
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                    BitmapFactory.decodeFile(myFile.path),
                    180
                )
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                    BitmapFactory.decodeFile(myFile.path),
                    270
                )
                ExifInterface.ORIENTATION_NORMAL -> BitmapFactory.decodeFile(myFile.path)
                else -> BitmapFactory.decodeFile(myFile.path)
            }
        } else {
            result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
        }

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