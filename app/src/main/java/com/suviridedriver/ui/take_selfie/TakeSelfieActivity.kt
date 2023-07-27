package com.suviridedriver.ui.take_selfie

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.suviridedriver.databinding.ActivityTakeSelfieBinding
import com.suviridedriver.utils.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class TakeSelfieActivity : BaseActivity() {
    private lateinit var binding: ActivityTakeSelfieBinding
    var context: Context = this

    val takeSelfieViewModel by viewModels<TakeSelfieViewModel>()

    var SELECT_PICTURE = 200
    val CAMERA_REQUEST_CODE = 102
    val CAMERA_PERM_CODE = 101

    var frontImage: Bitmap? = null
    var backImage: Bitmap? = null

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeSelfieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLaunchCamera.setOnClickListener {
            uploadImage()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                getBitmap(data)
                showLog("data_image", "device - " + data.toString())
            }

            if (requestCode == CAMERA_REQUEST_CODE) {
                showLog("data_image", "camera - " + data.toString())
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val photo = data.extras!!["data"] as Bitmap?
                    // getMultipartImage(photo!!)
                    // binding.ivUserPic.setImageBitmap(photo)
                    saveImage(photo)
                }
            }
        }
    }

    private fun getBitmap(data: Intent?) {
        // Get the url of the image from data
        val selectedImageUri: Uri? = data!!.data
        if (null != selectedImageUri) {
            // update the preview image in the layout
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
            // getMultipartImage(bitmap)
            // binding.ivUserPic.setImageBitmap(bitmap)
            saveImage(bitmap)
        }
    }

    private fun uploadImage() {
        // setup the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Upload Image")
        builder.setMessage("Choose one of the options")
        // add a button
        builder.setPositiveButton(
            "Camera"
        ) { dialogInterface: DialogInterface, i: Int ->
            askCameraPermissions()
        }
        builder.setNegativeButton(
            "Gallery"
        ) { dialogInterface: DialogInterface, i: Int ->
            imageChooser()
        }
        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // this function is triggered when
    // the Select Image Button is clicked
    fun imageChooser() {
        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    open fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                CAMERA_PERM_CODE
            )
        } else {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }
        }
    }


    private fun saveImage(bitmap: Bitmap?) {
        // val bitmap = BitmapFactory.decodeResource(resources, com.suviriderider.R.drawable.apple)
        // convert to bitmap to file
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val selfie: MultipartBody.Part = MultipartBody.Part.createFormData(
            "selfie",
            "selfie.png",
            RequestBody.create("image/*".toMediaTypeOrNull(), bos.toByteArray())
        )

        if (selfie != null){
            takeSelfie(selfie)
        }else{
            showToast("Resubmit image of doc")
        }

        if (frontImage == null) {
            frontImage = bitmap
            binding.ivUserImage.setImageBitmap(frontImage)
            // submitDrivingLicenceRequest.uploadImage = uploadImage
        } else {
            backImage = bitmap
            binding.ivUserImage.setImageBitmap(backImage)
            // submitDrivingLicenceRequest.uploadImage = uploadImage
        }

        if (frontImage != null && backImage != null) {
            //submitDrivingLicenceDetails(submitDrivingLicenceRequest)
        }
    }

    private fun takeSelfie(selfieImage: MultipartBody.Part) {
        takeSelfieViewModel.takeSelfie(selfieImage)
        takeSelfieViewModel.takeSelfieResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                        AppSession.save(context,Constants.FILED_TYPE,it.data.nextFiled)
                        finish()
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(Constants.TAG, "Loading")
                }
            }
        }
    }

}