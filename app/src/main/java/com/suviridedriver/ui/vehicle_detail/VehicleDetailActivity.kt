package com.suviridedriver.ui.vehicle_detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.suviridedriver.databinding.ActivityVehicleDetailBinding
import com.suviridedriver.model.vehicle_detail.AllVehical
import com.suviridedriver.model.vehicle_detail.VehicleDetailRequest
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.util.*

@AndroidEntryPoint
class VehicleDetailActivity : BaseActivity() {
    private  var allVehicals: List<AllVehical>? = null
    private lateinit var binding: ActivityVehicleDetailBinding
    var context: Context = this
    val vehicleDetailViewModel by viewModels<VehicleDetailViewModel>()
    var vehicleDetailRequest = VehicleDetailRequest()
    var isReadyToUploadImage = false

    var SELECT_PICTURE = 200
    val CAMERA_REQUEST_CODE = 102
    val CAMERA_PERM_CODE = 101

    var frontImage: Bitmap? = null
    var backImage: Bitmap? = null

    // Spinner Drop down elements
    val categories: MutableList<String> = ArrayList()

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getVehicleTypes()

        // Spinner click listener
        binding.spinnerVehicleType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
           try {
               (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
               (parent.getChildAt(0) as TextView).setTextSize(12f)
               val gender = (parent.getChildAt(0) as TextView).text
               // showToast(gender.toString())
               vehicleDetailRequest.vehicleType = gender.toString()
               if (allVehicals != null && position != 0){
                   val idIndex = position - 1
                   AppSession.save(context,Constants.VEHICLE_ID, allVehicals!!.get(idIndex)._id)
                   showToast("${allVehicals!!.get(idIndex).name} - ${allVehicals!!.get(idIndex)._id}")
               }
           }catch (e:java.lang.Exception){
               e.printStackTrace()
           }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        categories.add("Select Vehicle Type")
       // categories.add("bike")
       // categories.add("car")
       // categories.add("auto")
        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        binding.spinnerVehicleType.setAdapter(dataAdapter)

        binding.edtVehicleModelNumber.filters=arrayOf(
            InputFilter.LengthFilter(30),
            InputFilter.AllCaps(),getEditTextFilterLicence())
        binding.etdRegistrationID.filters=arrayOf(
            InputFilter.LengthFilter(15),
            InputFilter.AllCaps(),getEditTextFilterLicence())

        binding.ivBack.setOnClickListener {
            if (isReadyToUploadImage) {
                isReadyToUploadImage = false
                binding.rlUploadImage.visibility = View.INVISIBLE
                binding.llMiddle.visibility = View.VISIBLE
            } else {
                finish()
            }
        }

        binding.tvDOR.setOnClickListener {
            showDate(binding.tvDOR)
        }

        binding.tvDORV.setOnClickListener {
            showValidityDate(binding.tvDORV)
        }

        binding.tvNext.setOnClickListener {
            vehicleDetailRequest.vehicleModelNumber = binding.edtVehicleModelNumber.text.toString()
            vehicleDetailRequest.registrationID = binding.etdRegistrationID.text.toString()
            vehicleDetailRequest.dateofRegistration = binding.tvDOR.text.toString()
            vehicleDetailRequest.registrationValidity = binding.tvDORV.text.toString()

            if (vehicleDetailViewModel.validateCredentials(vehicleDetailRequest).first) {
                if (!isReadyToUploadImage) {
                    isReadyToUploadImage = true
                    binding.rlUploadImage.visibility = View.VISIBLE
                    binding.llMiddle.visibility = View.INVISIBLE
                }
            } else {
                showToast((vehicleDetailViewModel.validateCredentials(vehicleDetailRequest).second))
            }
        }

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
        val imageOfRegistrationCard: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            "Image.png",
            RequestBody.create("image/*".toMediaTypeOrNull(), bos.toByteArray())
        )

        if (imageOfRegistrationCard !=null){
            vichaluploadimage(imageOfRegistrationCard)
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
        vehicleDetailViewModel.imageupload(image)
        vehicleDetailViewModel.submitVehiclimage.observe(this) {

            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                     //   Log.d(Constants.TAG, "Success - " + it.data.toString())


                        vehicleDetailRequest.imageOfRegistrationCard = it.data.imageUrl

                        if (vehicleDetailViewModel.validateCredentials(vehicleDetailRequest).first) {

                            submitVehicleDetail(vehicleDetailRequest)

                        } else {
                            showToast((vehicleDetailViewModel.validateCredentials(vehicleDetailRequest).second))
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


    private fun submitVehicleDetail(vehicleDetailRequest: VehicleDetailRequest) {

      /*  showToast(vehicleDetailRequest.vehicleType)
        showToast(vehicleDetailRequest.vehicleModelNumber)
        showToast(vehicleDetailRequest.registrationID)
        showToast(vehicleDetailRequest.dateofRegistration.toString())
        showToast(vehicleDetailRequest.registrationValidity.toString())
        showToast(vehicleDetailRequest.imageOfRegistrationCard.toString())
*/

        vehicleDetailViewModel.submitVehicleDetails(vehicleDetailRequest)
        vehicleDetailViewModel.submitVehicleDetailsLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, "Success - "+it.data.toString())
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

    private fun getVehicleTypes() {
        vehicleDetailViewModel.getVehicleTypes()
        vehicleDetailViewModel.vehicleTypesLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, "Success - "+it.data.toString())
                        //Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                        allVehicals =  it.data.result
                        if (allVehicals !=null){

                            for (i in 0 until it.data.result.size)
                            {
                                categories.add(it.data.result.get(i).name)
                            }
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
                else -> {}
            }
        }
    }

}