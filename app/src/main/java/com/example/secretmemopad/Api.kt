package com.example.secretmemopad

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @POST("login")
    fun loginapi(@Body request: Login): Call<UserApi>

    @POST("register")
    fun register(@Body request: Register): Call<UserApi>

    @GET("notes")
    fun getNotes(@Query("user_id") userId: Int): Call<NoteResponse>

    @POST("notes")
    fun submitNote(@Body note: Noting): Call<Void>
}