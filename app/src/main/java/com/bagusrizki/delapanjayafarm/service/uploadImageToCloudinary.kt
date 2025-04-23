package com.bagusrizki.delapanjayafarm.service

import android.graphics.Bitmap
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

fun uploadImageToCloudinary(
    bitmap: Bitmap,
    onSuccess: (String) -> Unit,
    onError: (Exception) -> Unit
) {

    val client = OkHttpClient()
    val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dyxw8ym4m/image/upload"
    val apiKey = "554625621944331"
    val uploadPreset = "imageTest"

    // Konversi Bitmap ke ByteArray
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val imageData = byteArrayOutputStream.toByteArray()

    // Membuat body permintaan
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",
            "image.jpg",
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageData)
        )
        .addFormDataPart("upload_preset", uploadPreset)
        .addFormDataPart("api_key", apiKey)
        .build()

    val request = Request.Builder()
        .url(cloudinaryUrl)
        .post(requestBody)
        .build()

    // Eksekusi permintaan
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                // Parse URL dari JSON respon
                val url = JSONObject(responseBody ?: "").getString("secure_url")
                onSuccess(url)
            } else {
                onError(Exception("Failed to upload image"))
            }
        }
    })
}