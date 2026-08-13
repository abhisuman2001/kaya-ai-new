package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class BackendLoginRequest(
    val email: String,
    val password: String
)

data class BackendRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val company: String = "BuildTech Global",
    val role: String = "SUPERVISOR",
    val job_title: String = "Safety Inspector"
)

data class BackendTokenResponse(
    val access_token: String,
    val token_type: String = "bearer",
    val user_id: String,
    val name: String,
    val role: String
)

data class BackendUserDetailResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val job_title: String? = null,
    val company: String? = null,
    val site_location: String? = null,
    val connected_glasses_model: String? = null,
    val theme: String? = null,
    val language: String? = null,
    val avatar_url: String? = null,
    val is_google_auth: Boolean? = false
)

data class BackendUserProfileUpdateRequest(
    val user_id: String,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val job_title: String? = null,
    val company: String? = null,
    val site_location: String? = null,
    val connected_glasses_model: String? = null,
    val theme: String? = null,
    val language: String? = null,
    val avatar_url: String? = null
)

interface BackendApiService {

    @POST("auth/login")
    suspend fun login(@Body request: BackendLoginRequest): Response<BackendTokenResponse>

    @POST("auth/register")
    suspend fun register(@Body request: BackendRegisterRequest): Response<BackendTokenResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Query("user_id") userId: String? = null,
        @Query("email") email: String? = null
    ): Response<BackendUserDetailResponse>

    @POST("auth/profile")
    suspend fun updateProfile(@Body request: BackendUserProfileUpdateRequest): Response<Map<String, Any>>
}

object BackendApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/" // Local FastAPI Server Android loopback

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val apiService: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BackendApiService::class.java)
    }
}
