package com.suviridedriver.ui.personal_details

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityProfileBinding
import com.suviridedriver.model.update_profile.UpdateProfileRequest
import com.suviridedriver.ui.update_personal_details.UpdateProfileViewModel
import com.suviridedriver.ui.welcome.WelcomeActivity
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    val context: Context = this
    private val profileViewModel by viewModels<ProfileViewModel>()
    val updateProfileViewModel by viewModels<UpdateProfileViewModel>()
    val updateProfileRequest = UpdateProfileRequest()

    @Inject
    lateinit var tokenManager: TokenManager

    // constant to compare
    // the activity result code
    var SELECT_PICTURE = 200
    val CAMERA_REQUEST_CODE = 102
    val CAMERA_PERM_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
        /*    val name = AppSession.getValue(context, Constants.USER_NAME)!!
            val number = AppSession.getValue(context, Constants.USER_MOBILE_NUMBER)!!
            val profile_pic = AppSession.getValue(context, Constants.PROFILE_PIC)!!
            val address = AppSession.getValue(context, Constants.ADDRESS)!!*/
            /*Glide.with(this).load(profile_pic).placeholder(R.drawable.profile_icon).into(binding.ivUserPic)
            showLog("profile_pic", profile_pic)

            *//* binding.edtUserName.hint = name
             binding.edtUserNumber.hint = number
             binding.edtUseraddress.hint = address*//*

            binding.edtUserName.setText(name)
            binding.edtUserNumber.setText(""+number)
            binding.edtUseraddress.setText(address)*/

          //  updateProfileRequest.fullName = name
          //  updateProfileRequest.mobileNumber = "+$number"
            //updateProfileRequest.address = address

           // getProfileImage(profile_pic)
            getPprofiielObserver()
            profileViewModel.getDriverDetail()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*  binding.llDeleteAccount.setOnClickListener {
              showToast("Delete Account")
          }*/

        binding.ivBack.setOnClickListener {
            finish()
        }


        binding.tvUpdate.setOnClickListener {

            val updatedName = binding.edtUserName.text.toString().trim()
            val updatedAddress = binding.edtUseraddress.text.toString().trim()

            if (!updatedName.equals("")) {
                updateProfileRequest.fullName = updatedName
            }

            if (!updatedAddress.equals("")) {
                updateProfileRequest.address = updatedAddress
            }

            updateProfileViewModel.updateUserProfile(updateProfileRequest)

           /* if (updateProfileRequest.selfie != null){
               // updateProfileViewModel.updateUserProfile(updateProfileRequest)
                showLog("updateProfileRequest", updateProfileRequest.toString())
                finish()
            }else{

            }*/

        }



        updateProfileViewModel.updateProfileResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, it.data.toString())
                        showToast(it.data.message)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }

        // handle the Choose Image button to trigger
        // the image chooser function
        binding.ivCameraIcon.setOnClickListener {
            uploadImage()
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

    private fun getProfileImage(profileUrl: String) {
        try {
            if (!profileUrl.isNullOrBlank()) {
                Glide.with(context)
                    .asBitmap()
                    .load(profileUrl)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            getMultipartImage(resource)
                            showLog("image", "load from uri")
                        }
                    })
            } else {
                val resource = BitmapFactory.decodeResource(resources, R.drawable.profile_icon)
                getMultipartImage(resource)
                showLog("image", "load from local")
            }
        } catch (e: Exception) {
            showLog("image", e.message)
            val resource = BitmapFactory.decodeResource(resources, R.drawable.profile_icon)
            getMultipartImage(resource)
        }
    }

    private fun getMultipartImage(resource: Bitmap) {
        val bos = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val uploadImage: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            "selfie.png",
            RequestBody.create("image/*".toMediaTypeOrNull(), bos.toByteArray())
        )
        showLog("image", "getting image successfully.")

        if (uploadImage !=null){
            vichaluploadimage(uploadImage)
        }else{
            showToast("Resubmit image of doc")
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_account_bottom_sheet_layout)
        val call = dialog.findViewById<TextView>(R.id.tvCall)
        val cancel = dialog.findViewById<TextView>(R.id.tvCancel)

        call.setOnClickListener {
            deleteUser(dialog)
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun deleteUser(dialog: Dialog) {
        profileViewModel.deleteDriver()
        profileViewModel.deleteDriverResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    showToast(it.data!!.message)
                    showLog(TAG, "Success - " + it.data)
                    showToast(it.data.message)
                    dialog.dismiss()
                    TokenManager.saveToken("", this)
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
                is NetworkResult.Error -> {
                    showToast(it.message)
                    showLog(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    showLog(TAG, "Loading")
                }
            }
        }
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
                    getMultipartImage(photo!!)
                    binding.ivUserPic.setImageBitmap(photo)
                }
            }
        }
    }

    private fun getPprofiielObserver()
    {

        profileViewModel.getDriverDetailResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        showLog("driver_details", "loginResponse " + it.data)
                        val data = it.data
                        val drivingStatus = data.drivingLicence.documentSubmission
                        val vehiclesStatus = data.vehiclesDetails.documentSubmission
                        val addressStatus = data.drivingLicence.address
                        val takeSelfieStatus = data.takeSelfie.documentSubmission
                        val paymentStatus = data.paymentVerification

                        Glide.with(this).load(data.takeSelfie.selfie).placeholder(R.drawable.profile_icon).into(binding.ivUserPic)
                       // showLog("profile_pic", profile_pic)

                        /* binding.edtUserName.hint = name
                         binding.edtUserNumber.hint = number
                         binding.edtUseraddress.hint = address*/

                        binding.edtUserName.setText(data.drivingLicence.fullName)
                        binding.edtUserNumber.setText(""+data.mobileNumber)
                        binding.edtUseraddress.setText(data.drivingLicence.address)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("driver_details", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
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
            getMultipartImage(bitmap)
            binding.ivUserPic.setImageBitmap(bitmap)
        }
    }


    private fun vichaluploadimage(image: MultipartBody.Part) {
        updateProfileViewModel.imageupload(image)
        updateProfileViewModel.submitProfilelimage.observe(this) {

            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        //   Log.d(Constants.TAG, "Success - " + it.data.toString())
                        updateProfileRequest.selfie = it.data.imageUrl

                        updateProfileViewModel.updateUserProfile(updateProfileRequest)


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