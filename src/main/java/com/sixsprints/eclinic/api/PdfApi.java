package com.sixsprints.eclinic.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PdfApi {

  @Multipart
  @POST("/convert/html")
  Call<ResponseBody> upload(@Part MultipartBody.Part file, @Part("marginTop") RequestBody marginTop,
    @Part("marginRight") RequestBody marginRight, @Part("marginBottom") RequestBody marginBottom,
    @Part("marginLeft") RequestBody marginLeft);

}
