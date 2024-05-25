package com.underoid.ecogreen.network

import com.underoid.ecogreen.model.Location
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/")
    fun getLocations(): Call<List<Location>>

    @POST("/locations/{id}")
    suspend fun sendLocation(@Path("id") id: Int, @Body location: Location)
}