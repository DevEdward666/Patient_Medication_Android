package com.example.newpc.services;

import com.example.newpc.entities.Patients;
import com.example.newpc.model.AppVersionResponse;
import com.example.newpc.model.FileInfo;
import com.example.newpc.model.LoginResponse;
import com.example.newpc.model.LogsResponse;
import com.example.newpc.model.MedsResponse;
import com.example.newpc.model.MedsSpeciificResponse;
import com.example.newpc.model.PatientDataResponse;
import com.example.newpc.model.ResObj;
import com.example.newpc.model.UserModelResponse;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientService {
    @FormUrlEncoded
    @POST("api/patients/getspecificmedications")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<MedsSpeciificResponse> getspecificmedications(
            @Header("Authorization") String auth,
            @Field("patno") String patno,
            @Field("chargeslip") String chargeslip,
            @Field("stockcode") String stockcode
    );
    @FormUrlEncoded
    @POST("api/patients/getlogsbypatno")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<LogsResponse> getlogsbypatno(
            @Header("Authorization") String auth,
            @Field("patno") String patno

    );
    @FormUrlEncoded
    @POST("api/patients/getLogs")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<LogsResponse> getLogs(
            @Header("Authorization") String auth,
             @Field("user") String user,
            @Field("limit") int limit
    );
    @FormUrlEncoded
    @POST("api/patients/getlogsbydate")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<LogsResponse> getLogsbydate(
            @Header("Authorization") String auth,
            @Field("year") int year,
            @Field("month") int month,
            @Field("dayofmonth") int dayofmonth
    );
    @FormUrlEncoded
    @POST("api/patients/inserttomedicationhistory")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ResponseBody> inserttomedicationhistory(
            @Header("Authorization") String auth,
            @Field("patno") String patno,
            @Field("chargeslip") String chargeslip,
            @Field("stockcode") String stockcode
    );
    @FormUrlEncoded
    @POST("api/patients/getallmedications")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<MedsResponse> getallmedications(
            @Header("Authorization") String auth,
            @Field("patno") String patno
    );
    @POST("api/user/getUserInfo")
    Call<UserModelResponse> db_get_active_user(
            @Header("Authorization") String auth
    );

    @FormUrlEncoded
    @POST("api/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<LoginResponse> userLogin(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type
    );
    @FormUrlEncoded
    @POST("api/patients/patientinformationwithoutmeds")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<PatientDataResponse> PatientList(
            @Header("Authorization") String auth,
            @Field("patno") String patno
    );
    @FormUrlEncoded
    @POST("api/android/getdetails")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<AppVersionResponse> AndroidVersion(
            @Header("Authorization") String auth,
            @Field("AppName") String AppName
    );
    @Multipart
    @POST("api/file/upload")
    Call<RequestBody> upload(@Part MultipartBody.Part photo, @Part("description") RequestBody requestBody);
}
