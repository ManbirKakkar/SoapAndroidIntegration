package com.asml.pdscanner.network.soap

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SoapService {
    @Headers("Content-Type: text/xml")
    @POST("calculator.asmx")
    suspend fun addNumbers(@Body request: RequestBody): ResponseBody
}
