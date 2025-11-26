package com.example.tourguideapp


import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


class Backend {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun uploadImageToBackend(path: String, callback: (String?) -> Unit) {
        val file = File(path)
        if (!file.exists()) {
            callback(null)
            return
        }

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, requestBody)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/recognize-landmark") //android alias, change if not running on emulator, otherwise leave like this
            .post(multipartBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    if (!resp.isSuccessful) {
                        callback(null)
                        return
                    }

                    val body = resp.body?.string()
                    if (body.isNullOrEmpty()) {
                        callback(null)
                        return
                    }
                    val json = JSONObject(body)
                    val landmark = json.optString("landmark_name", null)
                    callback(landmark)
                }
            }
        })
    }


    fun fetchStoryFromBackend(
        landmark: String,
        style: String,
        tone: String,
        length: String,
        callback: (String?) -> Unit
    ) {
        val json = JSONObject().apply {
            put("landmark", landmark)
            put("style", style)
            put("tone", tone)
            put("length", length)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/generate-story")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    if (!resp.isSuccessful) {
                        callback(null)
                        return
                    }

                    val body = resp.body?.string()
                    if (body.isNullOrEmpty()) {
                        callback(null)
                        return
                    }

                    val jsonResp = JSONObject(body)
                    // We could also read "summary" if needed
                    val story = jsonResp.optString("story", null)
                    callback(story)
                }
            }
        })
    }




}

