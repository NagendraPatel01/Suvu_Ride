package com.suviridedriver.api

import com.suviride.model.success_response.SuccessResponse
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridecustomer.model.screenLabel.ScreenLabelResponse
import com.suviridedriver.model.back_detail.BankDetailResponse
import com.suviridedriver.model.back_detail.BankDetailRequest
import com.suviridedriver.model.collect_payment.CollectPayment
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.driver_details.VehiclesDetails
import com.suviridedriver.model.driver_status.DriverStatusRequest
import com.suviridedriver.model.driver_status.DriverStatusResponse
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceRequest
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceResponse
import com.suviridedriver.model.earnings.GetEarningResponse
import com.suviridedriver.model.get_driver.GetDriverRequest
import com.suviridedriver.model.get_driver.GetDriverResponse
import com.suviridedriver.model.get_ongoing_ride.GetOngoingRideResponse
import com.suviridedriver.model.get_profile.GetProfileResponse
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.model.issue_submit.IssueSubmitRequest
import com.suviridedriver.model.languages.GetLanguagesResponse
import com.suviridedriver.model.login.LoginRequest
import com.suviridedriver.model.login.LoginResponse
import com.suviridedriver.model.make_payment.MakePaymentRequest
import com.suviridedriver.model.make_payment.PaymentSuccessResponse
import com.suviridedriver.model.navigate_to_pickup_point.NavigateToPickupPointResponse
import com.suviridedriver.model.rating.RatingRequest
import com.suviridedriver.model.registration.RegistrationResponse
import com.suviridedriver.model.ride_packages.RidesPackagesResponse
import com.suviridedriver.model.ride_request.AcceptRideResponse
import com.suviridedriver.model.ride_request.RejectRideRespone
import com.suviridedriver.model.ride_request.RideRequest
import com.suviridedriver.model.rides.RidesResponse
import com.suviridedriver.model.start_ride.StartRideResponse
import com.suviridedriver.model.state_city.AddStateCityRequest
import com.suviridedriver.model.state_city.CityResponse
import com.suviridedriver.model.state_city.StateResponse
import com.suviridedriver.model.take_selfie.TakeSelfieResponse
import com.suviridedriver.model.total_and_remaining_rides.TotalRemainingRidesResponse
import com.suviridedriver.model.update_fcm.UpdateFCMToken
import com.suviridedriver.model.update_location.UpdateLocationRequest
import com.suviridedriver.model.update_location.UpdateLocationResponse
import com.suviridedriver.model.update_profile.UpdateProfileRequest
import com.suviridedriver.model.update_profile.UpdateProfileResponse
import com.suviridedriver.model.vehicle_detail.VehicleDetailRequest
import com.suviridedriver.model.vehicle_detail.VehicleDetailResponse
import com.suviridedriver.model.vehicle_detail.VehiclesTypesResponse
import com.suviridedriver.model.verification.DocumentsVerificationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface APIServices {
    @POST("admin/getAllScreensByLanguage")
    suspend fun getAllScreensByLanguage(@Body getAllScreenRequest: ScreenLabelRequest): Response<ScreenLabelResponse>

    @GET("admin/getActiveLanguages")
    suspend fun getAllLanguages(): Response<GetLanguagesResponse>

    @GET("driver/getAllVehicals")
    suspend fun getVehicleTypes(): Response<VehiclesTypesResponse>

    @POST("driver/driverLogin")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @PATCH("driver/updateDriverStatus")
    suspend fun updateDriverStatus(@Body driverStatusRequest: DriverStatusRequest): Response<DriverStatusResponse>

    @POST("driver/drivingLicence")
    suspend fun submitDrivingLicenceDetails(@Body drivingLicence: SubmitDrivingLicenceRequest): Response<SubmitDrivingLicenceResponse>


    /*@Multipart
    @POST("driver/vehicleDetails")
    suspend fun submitVehicleDetails(
        @Part("vehicleModelNumber") vehicleModelNumber: String,
        @Part("registrationID") registrationID: String,
        @Part("dateofRegistration") dateofRegistration: String,
        @Part("registrationValidity") registrationValidity: String,
        @Part("vehicleType") vehicleType: String,
        @Part imageOfRegistrationCard: MultipartBody.Part
    ): Response<VehicleDetailResponse>
*/


    @POST("driver/vehicleDetails")
    suspend fun submitVehicleDetails(@Body vehiclesDetails: VehicleDetailRequest): Response<VehicleDetailResponse>

// imageupload

    @Multipart
    @POST("driver/uploadimage")
    suspend fun imageupload(@Part image: MultipartBody.Part): Response<ImageuploadRespons>


    @Multipart
    @POST("driver/")
    suspend fun submitBankDetails(@Body bankDetailRequest: BankDetailRequest): Response<BankDetailResponse>

    @GET("driver/getAllRidesPackages")
    suspend fun getRidesPackages(@Query("vehicle") vehicleId:String): Response<RidesPackagesResponse>

    @Multipart
    @POST("driver/takeSelfie")
    suspend fun takeSelfie(@Part selfieImage: MultipartBody.Part): Response<TakeSelfieResponse>

    @GET("driver/driverDocumentsVerification")
    suspend fun documentsVerification(): Response<DocumentsVerificationResponse>

    @POST("customer/allNearestDrivers")
    suspend fun getDrivers(@Body getDriverRequest: GetDriverRequest): Response<GetDriverResponse>

    @PATCH("driver/updateDriverCurrentLocation")
    suspend fun updateLocation(@Body updateLocationRequest: UpdateLocationRequest): Response<UpdateLocationResponse>

    @GET("driver/getTotalRidesWithStatus")
    suspend fun getRides(): Response<RidesResponse>

    @GET("driver/totalEarning")
    suspend fun getEarning(): Response<GetEarningResponse>

    @GET("driver/getremainingAndTotalRides")
    suspend fun getremainingAndTotalRides(): Response<TotalRemainingRidesResponse>

    @POST("driver/acceptRideRequest")
    suspend fun acceptRide(@Body rideRequest: RideRequest): Response<AcceptRideResponse>

    @POST("driver/declineRideRequest")
    suspend fun declineRide(@Body rideRequest: RideRequest): Response<RejectRideRespone>

    // next apis

    // for checking that ride is ongoing or not
    @GET("driver/getOngoingRide")
    suspend fun getOngoingRide(): Response<GetOngoingRideResponse>

    // for start ride after accept ride request
    @POST("driver/startRide")
    suspend fun startRide(@Body rideRequest: RideRequest): Response<StartRideResponse>

    // for end ride
    @PATCH("driver/completeRide")
    suspend fun completeRide(@Body rideRequest: RideRequest): Response<StartRideResponse>

    // for get navigate time
   /* @POST("driver/navigateToPickupPoint")
    suspend fun navigateToPickupPoint(@Body navigateToPickupPointRequest: NavigateToPickupPointRequest): Response<NavigateToPickupPointResponse>
*/


    // for get navigate time
    @GET("driver/navigateToPickupPoint")
    suspend fun navigateToPickupPoint(@Query("distance") distance: Int): Response<NavigateToPickupPointResponse>


    // for get navigate time
    @GET("driver/reachedToDestination")
    suspend fun reachedToDestination(@Query("distance") navigateToPickupPointRequest: Int): Response<NavigateToPickupPointResponse>

    // for ride collect Payment
    @POST("driver/collectPayment")
    suspend fun collectPayment(@Body collectPayment: CollectPayment): Response<SuccessResponse>

    // for ride rating form driver
    @POST("driver/driverRating")
    suspend fun submitRating(@Body ratingRequest: RatingRequest): Response<SuccessResponse>

    @PATCH("driver/driverLogout")
    suspend fun driverLogout(): Response<SuccessResponse>

    // remaining API
    @POST("driver/paymentTransaction")
    suspend fun paymentSuccess(@Body mapPaymentRequest: MakePaymentRequest): Response<PaymentSuccessResponse>

    @POST("driver/writeToUs")
    suspend fun submitIssue(@Body issueSubmitRequest: IssueSubmitRequest): Response<SuccessResponse>

    @PATCH("driver/updatePersonalDetails")
    suspend fun updateUserProfile(
        @Body updateProfileRequest: UpdateProfileRequest
    ): Response<UpdateProfileResponse>


    @DELETE("driver/deleteDriver")
    suspend fun deleteDriver(): Response<SuccessResponse>

    @GET("driver/documentSubmission")
    suspend fun statusChecker(): Response<RegistrationResponse>

    // for update FCM Token
    @POST("driver/updateFCM")
    suspend fun updateFCM(@Body updateFCMToken: UpdateFCMToken): Response<SuccessResponse>

    // for get state
    @GET("admin/getActiveState")
    suspend fun getState(): Response<StateResponse>

    // for get cities
    @GET("admin/getActiveCities")
    suspend fun getCities(@Query("state") stateId:String): Response<CityResponse>

    // for update
    @POST("driver/personal-details")
    suspend fun updateStateCity(@Body addStateCityRequest: AddStateCityRequest): Response<SuccessResponse>

    // for end Ride Request
    @POST("driver/endRideRequest")
    suspend fun endRide(@Body rideRequest: RideRequest): Response<SuccessResponse>

    // for check Ride Status
    @GET("driver/checkRideStatus")
    suspend fun checkRideStatus(@Query ("ride_id") id:String): Response<SuccessResponse>



    // for get driver details
    @GET("driver/profile")
    suspend fun getDriverDetail(): Response<GetDriverDetailResponse>

}