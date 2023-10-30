package com.example.myapplication.data

import com.example.myapplication.data.Service
import retrofit2.Call
import retrofit2.http.GET

interface API {
    @GET("serviceInstance?page=0&size=2000") //endpoint
    fun getCompanies(): Call<List<Service>>
}