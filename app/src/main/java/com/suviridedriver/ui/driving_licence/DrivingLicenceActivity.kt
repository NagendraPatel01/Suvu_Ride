package com.suviridedriver.ui.driving_licence

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.suviridedriver.databinding.ActivityDrivingLicenceBinding
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceRequest
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.ACCESS_TOKEN
import com.suviridedriver.utils.Constants.ADDRESS
import com.suviridedriver.utils.Constants.DRIVER_ID
import com.suviridedriver.utils.Constants.LANGUAGE_ID
import com.suviridedriver.utils.Constants.USER_MOBILE_NUMBER
import com.suviridedriver.utils.Constants.USER_NAME
import com.suviridedriver.utils.Constants.USER_STATUS
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class DrivingLicenceActivity : BaseActivity() {
    private lateinit var binding: ActivityDrivingLicenceBinding
    var context: Context = this

    var SELECT_PICTURE = 200
    val CAMERA_REQUEST_CODE = 102
    val CAMERA_PERM_CODE = 101

    var frontImage: Bitmap? = null
    var backImage: Bitmap? = null
    var isReadyToUploadImage = false
    val submitDrivingLicenceRequest = SubmitDrivingLicenceRequest()
    val drivingLicenceViewModel by viewModels<DrivingLicenceViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingLicenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val screenLabelRequest = ScreenLabelRequest("driverScreens", AppSession.getValue(context, Constants.LANGUAGE_ID)!!)
        // getScreenLabel(screenLabelRequest)
        /* setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_header",
             binding.tvHeader
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_name",
             binding.tvLevelName
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_address",
             binding.tvLevelAddress
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_gender",
             binding.tvLevelGender
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_issue_date",
             binding.tvLevelIssuedDate
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_validity",
             binding.tvLevelValiditiy
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_description",
             binding.tvDescription
         )
         setScreenLabelData(
             Constants.DRIVING_LICENCE_SCREEN_CODE,
             "driving_licence_verify",
             binding.tvNext
         )*/

        binding.edtName.filters = arrayOf(InputFilter.LengthFilter(50), getEditTextFilterName())
        // binding.edtAddress.filters=arrayOf(InputFilter.LengthFilter(80),getEditTextFilterAddress())
        binding.edtLicenceNumber.filters =
            arrayOf(InputFilter.LengthFilter(18), InputFilter.AllCaps(), getEditTextFilterLicence())

        binding.ivBack.setOnClickListener {
            if (isReadyToUploadImage) {
                isReadyToUploadImage = false
                binding.rlUploadImage.visibility = View.INVISIBLE
                binding.llMiddle.visibility = View.VISIBLE
            } else {
                finish()
            }
        }

        binding.tvIssuedDate.setOnClickListener {
            showDate(binding.tvIssuedDate)
        }

        binding.tvValiditiy.setOnClickListener {
            showValidityDate(binding.tvValiditiy)
        }

        binding.tvNext.setOnClickListener {

            try {
                submitDrivingLicenceRequest.fullName = binding.edtName.text.toString()
                // submitDrivingLicenceRequest.address = binding.edtAddress.text.toString()
                submitDrivingLicenceRequest.licenceNumber = binding.edtLicenceNumber.text.toString()
                submitDrivingLicenceRequest.issuedDate = binding.tvIssuedDate.text.toString()
                submitDrivingLicenceRequest.validitiy = binding.tvValiditiy.text.toString()
                showLog("issuedDate", binding.tvIssuedDate.text.toString())

                if (drivingLicenceViewModel.validateCredentials(submitDrivingLicenceRequest).first) {
                    if (!isReadyToUploadImage) {
                        isReadyToUploadImage = true
                        binding.rlUploadImage.visibility = View.VISIBLE
                        binding.llMiddle.visibility = View.GONE
                    }
                } else {
                    showToast(
                        (drivingLicenceViewModel.validateCredentials(
                            submitDrivingLicenceRequest
                        ).second)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(e.message.toString())
            }


        }


        // Spinner click listener
        binding.spinnerGender.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).setTextSize(12f)
                val gender = (parent.getChildAt(0) as TextView).text
                submitDrivingLicenceRequest.gender = gender.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add("Select gender")
        categories.add("male")
        categories.add("female")
        // categories.add("others")
        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        binding.spinnerGender.setAdapter(dataAdapter)

        binding.tvUpload.setOnClickListener {
            uploadImage()
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
        val uploadImage: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            "Image.png",
            RequestBody.create("image/*".toMediaTypeOrNull(), bos.toByteArray())
        )

        if (uploadImage !=null){
            vichaluploadimage(uploadImage)
        }else{
            showToast("Resubmit image of doc")
        }

        if (frontImage == null) {
            frontImage = bitmap
            binding.ivDoc.setImageBitmap(frontImage)
            // submitDrivingLicenceRequest.uploadImage = uploadImage
        } else {
            backImage = bitmap
            binding.ivDoc.setImageBitmap(backImage)
            // submitDrivingLicenceRequest.uploadImage = uploadImage
        }

        if (frontImage != null && backImage != null) {
            //submitDrivingLicenceDetails(submitDrivingLicenceRequest)
        }
    }


    private fun vichaluploadimage(image: MultipartBody.Part) {
        drivingLicenceViewModel.imageupload(image)
        drivingLicenceViewModel.submitVehiclimage.observe(this) {

            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        //   Log.d(Constants.TAG, "Success - " + it.data.toString())

                        submitDrivingLicenceRequest.uploadImage = it.data.imageUrl

                        if (drivingLicenceViewModel.validateCredentials(submitDrivingLicenceRequest).first) {
                            submitDrivingLicenceDetails(submitDrivingLicenceRequest)
                        } else {
                            showToast((drivingLicenceViewModel.validateCredentials(submitDrivingLicenceRequest).second))
                        }

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





    fun submitDrivingLicenceDetails(sumDrivingLicenceRequest: SubmitDrivingLicenceRequest) {
        drivingLicenceViewModel.submitDrivingLicenceDetails(sumDrivingLicenceRequest)
        // Log.d(Constants.TAG, sumDrivingLicenceRequest.address.toString())
        drivingLicenceViewModel.submitDrivingLicenceLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                       finish()

                       /* if (it.data!! != null) {
                            Log.d(TAG, it.data.toString())
                            AppSession.save(context, USER_NAME, it.data.)
                            AppSession.save(
                                context,
                                USER_MOBILE_NUMBER,
                                it.data.mobileNumber.toString()
                            )
                            AppSession.save(context, LANGUAGE_ID, it.data.language)
                            AppSession.save(context, USER_STATUS, it.data.Status)

                            if (it.data.token != null) {
                                TokenManager.saveToken(it.data.token, this)
                                AppSession.save(context, ACCESS_TOKEN, it.data.token)
                                AppSession.save(context, DRIVER_ID, it.data.driverId)
                                AppSession.save(context, ADDRESS, it.data.address)
                                AppSession.save(context, Constants.FILED_TYPE, it.data.nextFiled)
                                finish()
                            }

                        } else {
                            showLog(TAG, "data is null")
                        }*/
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message.toString())
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(Constants.TAG, "Loading")
                }
            }
        }
    }
}
