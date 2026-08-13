package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val DEFAULT_SUPABASE_URL = "https://gjlblcrmqkbxlrqxffaz.supabase.co"
    private const val DEFAULT_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdqbGJsY3JtcWtieGxycXhmZmF6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODYzMjg1NTgsImV4cCI6MjEwMTkwNDU1OH0.bUvBrqz_smRa6HI2AsCXemGTL3wsAHuoSuudveTZ5Wk"
    private const val DEFAULT_SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdqbGJsY3JtcWtieGxycXhmZmF6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4NjMyODU1OCwiZXhwIjoyMTAxOTA0NTU4fQ.d3d59Ff-34ijWjjoIDfJ4TmwGyUuAQQLM4mItqrmFsU"

    val supabaseUrl: String
        get() {
            val url = try { BuildConfig.SUPABASE_URL } catch (e: Throwable) { "" }
            return if (url.isNotBlank() && url.startsWith("http")) {
                if (url.endsWith("/")) url else "$url/"
            } else {
                "$DEFAULT_SUPABASE_URL/rest/v1/"
            }
        }

    val supabaseKey: String
        get() {
            val key = try { BuildConfig.SUPABASE_ANON_KEY } catch (e: Throwable) { "" }
            return if (key.isNotBlank()) key else DEFAULT_ANON_KEY
        }

    val serviceRoleKey: String
        get() {
            val key = try { BuildConfig.SUPABASE_SERVICE_ROLE_KEY } catch (e: Throwable) { "" }
            return if (key.isNotBlank()) key else DEFAULT_SERVICE_ROLE_KEY
        }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val keyToUse = serviceRoleKey
        val requestBuilder = original.newBuilder()
            .header("apikey", keyToUse)
            .header("Authorization", "Bearer $keyToUse")
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: KayaApiService by lazy {
        val baseUrl = if (supabaseUrl.endsWith("rest/v1/")) supabaseUrl else "${supabaseUrl}rest/v1/"
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(KayaApiService::class.java)
    }
}

