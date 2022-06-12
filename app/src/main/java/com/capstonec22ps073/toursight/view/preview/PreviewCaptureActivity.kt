package com.capstonec22ps073.toursight.view.preview

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityPreviewCaptureBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.tflite.Classifier
import com.capstonec22ps073.toursight.util.*
import com.capstonec22ps073.toursight.view.detail.DetailLandmarkActivity
import com.capstonec22ps073.toursight.view.main.MainViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class PreviewCaptureActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityPreviewCaptureBinding
    private lateinit var viewModel: PreviewViewModel

    private val inputSize = 128
    private val modelPath = "model_1_3.tflite"
    private val labelPath = "label.txt"
    private lateinit var classifier: Classifier
    private var token = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initClassifier()

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application, AuthRepository(pref), CulturalObjectRepository())
        ).get(
            PreviewViewModel::class.java
        )

        viewModel.getUserToken().observe(this) { token ->
            if (token != "") {
                this.token = token
            }
        }

        viewModel.getUsername().observe(this) { username ->
            if (username != "") {
                this.username = username
            }
        }

        val status = intent.getStringExtra("status")
        if (status == "gallery") {
            setImageFromGallery()
        } else {
            setImageFromCameraX()
        }

        viewModel.culturalObject.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    response.data?.let { culturalObjectsResponse ->
                        val intent = Intent(this, DetailLandmarkActivity::class.java)
                        intent.putExtra(DetailLandmarkActivity.DATA, culturalObjectsResponse[0])
                        intent.putExtra(DetailLandmarkActivity.SOURCE, "camera")

                        val optionCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@PreviewCaptureActivity,
                                Pair(binding.previewImageView, "culturalObject")
                            )

                        startActivity(intent, optionCompat.toBundle())
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                        if (message == "Token expired" || message == "Wrong Token or expired Token") {
                            AlertDialog.Builder(this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.token_exp_message))
                                .setCancelable(false)
                                .setPositiveButton("Ok") { _, _ ->
                                    viewModel.removeUserDataFromDataStore()
                                }
                                .show()
                        } else if (message == "no internet connection") {
                            showDialogNoConnection()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }

        viewModel.imageUploaded.observe(this) { imageResponse ->
            when (imageResponse) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    imageResponse.data?.let { _ ->
                        Log.d(TAG, "Image uploaded successfully")
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    imageResponse.message?.let { message ->
                        Log.e(TAG, "An error occurred (image upload): $message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnUpload.setOnClickListener {
            showLoading(true)
            binding.btnUpload.isEnabled = false
            lifecycleScope.launch(Dispatchers.Default) {

                val bitmap = BitmapFactory.decodeFile(getFile?.path)
                val result = classifier.recognizeImage(bitmap)

                launch (Dispatchers.Main){
                    if (result.isEmpty()) {
                        showErrorImageRecognitionFailed()
                        Log.d("Hasil Result", "klasifikasi gagal")
                    } else {
                        viewModel.getCulturalObjectByClassname(this@PreviewCaptureActivity.token, result[0].title)
                        uploadImage()

                        Log.d(
                            "Hasil Result",
                            result[0].title + String.format(
                                " Confidence : %.2f",
                                (result[0].confidence)
                            )
                        )
                    }
                    binding.btnUpload.isEnabled = true
                    showLoading(false)
                }
            }
        }

    }

    private fun showDialogNoConnection() {
        val dialog = CustomDialog(this, true, R.string.no_internet, R.string.no_internet_message)
        dialog.startDialogError()
    }

    private fun uploadImage() {
        val file = reduceFileImage(getFile as File)

        val username =
            this.username.toRequestBody("text/plain".toMediaType())

        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestImageFile
        )

        viewModel.uploadImage(this.token, imageMultipart, username)
    }

    private fun initClassifier() {
        classifier = Classifier(assets, modelPath, labelPath, inputSize)
    }

    private fun setImageFromCameraX() {
        val myFile = intent.getSerializableExtra("picture") as File

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
                BitmapFactory.decodeFile(myFile.path)
            )
        }

        getFile = convertBitmapToFile(result)
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

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showErrorImageRecognitionFailed() {
        val errorDialog = CustomDialog(this, true, R.string.image_recognition_failed_title, R.string.image_recognition_failed_message)
        errorDialog.startDialogError()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val TAG = "PreviewActivity"
    }
}