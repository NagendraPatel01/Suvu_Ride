package com.suviridedriver.ui.make_payment.network;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("cftoken/order")
    Call<ExampleResponse> getToken(@Body JsonObject object);
}
