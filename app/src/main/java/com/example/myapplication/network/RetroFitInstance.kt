package com.example.myapplication.network

import android.content.ContentValues
import android.util.Log
import com.example.myapplication.data.API
import com.example.myapplication.data.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
const val BASE_URL = "https://serviceregistry.navelink.org/api/"

fun getServices(callback: (List<Service>?) -> Unit){
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    api.getCompanies().enqueue(object : Callback<List<Service>> {
        override fun onResponse(
            call: Call<List<Service>>,
            response: Response<List<Service>>
        ) {
            if (response.isSuccessful) {
                val serviceList = response.body() // save data in list

                if (serviceList != null) {
                    for (service in serviceList) {
                        Log.i(ContentValues.TAG, "onResponse: ID: ${service.id}")
                        Log.i(ContentValues.TAG, "onResponse: Name: ${service.name}")
                        Log.i(ContentValues.TAG, "onResponse: Keywords: ${service.keywords}")
                        Log.i(ContentValues.TAG, "onResponse: Geometry Type: ${service.geometry}")
                    }
                }
                callback(serviceList)
            }
        }

        override fun onFailure(call: Call<List<Service>>, t: Throwable) {
            Log.e(ContentValues.TAG, "Request failed: ${t.message}", t)
            callback(null)
        }
    })
}