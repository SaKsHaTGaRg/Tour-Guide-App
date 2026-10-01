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
    fun cancelRequests() = client.dispatcher.cancelAll()

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
            .url("${BuildConfig.BACKEND_BASE_URL}/recognize-landmark")
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

                    val body = try { resp.body?.string() } catch (_: IOException) { null }
                    if (body.isNullOrEmpty()) {
                        callback(null)
                        return
                    }
                    val landmark = parseText(body, "landmark_name")
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
            .url("${BuildConfig.BACKEND_BASE_URL}/generate-story")
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

                    val body = try { resp.body?.string() } catch (_: IOException) { null }
                    if (body.isNullOrEmpty()) {
                        callback(null)
                        return
                    }

                    val story = parseText(body, "story")
                    callback(story)
                }
            }
        })
    }




    private fun parseText(body: String, field: String): String? = try {
        (JSONObject(body).opt(field) as? String)?.trim()?.takeIf { it.isNotEmpty() }
    } catch (_: org.json.JSONException) {
        null
    }
}

