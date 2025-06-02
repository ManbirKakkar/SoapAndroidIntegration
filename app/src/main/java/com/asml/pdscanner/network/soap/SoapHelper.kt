package com.asml.pdscanner.network.soap

import com.asml.pdscanner.utils.ResultValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object SoapHelper {
    private const val BASE_URL = "http://www.dneonline.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val service = retrofit.create(SoapService::class.java)

    suspend fun addNumbers(a: Int, b: Int): ResultValue<Int> {
        return try {
            val soapRequest = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
              <soap12:Body>
                <Add xmlns="http://tempuri.org/">
                  <intA>$a</intA>
                  <intB>$b</intB>
                </Add>
              </soap12:Body>
            </soap12:Envelope>
        """.trimIndent()

            val requestBody = soapRequest.toRequestBody("text/xml".toMediaType())
            val response = service.addNumbers(requestBody)

            // Parse XML response
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(response.byteStream(), null)

            var eventType = parser.eventType
            var result: Int? = null

            while (eventType != XmlPullParser.END_DOCUMENT && result == null) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "AddResult") {
                    parser.next()
                    result = parser.text.toIntOrNull()
                }
                eventType = parser.next()
            }

            result?.let { ResultValue.Success(it) } ?: ResultValue.Failure(Exception("Invalid response"))
        } catch (e: Exception) {
            ResultValue.Failure(e)
        }
    }

}