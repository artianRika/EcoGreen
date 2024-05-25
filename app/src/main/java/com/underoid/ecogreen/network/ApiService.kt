package com.underoid.ecogreen.network

import com.underoid.ecogreen.model.Location
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    fun getLocations(): Call<List<Location>>
}