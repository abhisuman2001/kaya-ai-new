package com.example.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

data class SupabaseSignUpUserMetadata(
    val display_name: String? = null,
    val site_role: String? = "worker",
    val email_verified: Boolean = true
)

data class SupabaseSignUpRequestDto(
    val email: String,
    val password: String,
    val email_confirm: Boolean = true,
    val user_metadata: SupabaseSignUpUserMetadata? = null,
    val data: SupabaseSignUpUserMetadata? = null
)

data class SupabaseSignInRequestDto(
    val email: String,
    val password: String
)

data class SupabaseAuthUser(
    val id: String,
    val email: String? = null,
    val user_metadata: SupabaseSignUpUserMetadata? = null
)

data class SupabaseAuthResponseDto(
    val id: String? = null,
    val access_token: String? = null,
    val token_type: String? = null,
    val user: SupabaseAuthUser? = null,
    val error: String? = null,
    val error_description: String? = null,
    val msg: String? = null
)

data class SupabaseSiteEventDto(
    val id: String? = null,
    val type: String = "HAZARD",
    val title: String,
    val description: String? = null,
    val project_id: String = "proj_01",
    val created_by_label: String? = "Supervisor",
    val created_by_role: String? = "SUPERVISOR",
    val assigned_to: String? = null,
    val severity: String = "HIGH",
    val status: String = "OPEN",
    val created_at: String? = null
)

data class SupabaseReportDto(
    val id: String? = null,
    val project_id: String = "proj_01",
    val title: String,
    val summary: String? = null,
    val body: String? = null,
    val created_at: String? = null
)

data class SupabaseTaskDto(
    val id: String? = null,
    val project_id: String = "proj_01",
    val title: String,
    val description: String? = null,
    val status: String = "todo",
    val priority: String = "medium",
    val assigned_to: String? = null,
    val created_by: String? = null,
    val created_at: String? = null
)

data class SupabaseBlueprintDto(
    val id: String? = null,
    val project_id: String = "proj_01",
    val name: String,
    val code: String? = null,
    val revision: String? = null,
    val discipline: String? = null,
    val status: String = "APPROVED",
    val created_at: String? = null
)

data class SupabaseDeviceDto(
    val id: String? = null,
    val user_id: String? = null,
    val name: String,
    val firmware: String? = null,
    val battery_level: Int = 100,
    val connection_state: String = "CONNECTED",
    val project_id: String = "proj_01"
)

data class SupabaseProfileDto(
    val id: String,
    val display_name: String? = null,
    val email: String? = null,
    val site_role: String? = null,
    val approval_status: String? = "approved"
)

data class SupabaseUserRoleDto(
    val user_id: String,
    val role: String = "supervisor"
)

interface KayaApiService {

    @POST("/auth/v1/signup")
    suspend fun signUp(
        @Body request: SupabaseSignUpRequestDto
    ): Response<SupabaseAuthResponseDto>

    @POST("/auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body request: SupabaseSignInRequestDto
    ): Response<SupabaseAuthResponseDto>

    @POST("profiles")
    suspend fun createProfile(
        @Body profile: SupabaseProfileDto
    ): Response<List<SupabaseProfileDto>>

    @GET("site_events")
    suspend fun getHazards(
        @Query("select") select: String = "*",
        @Query("type") type: String = "eq.HAZARD"
    ): Response<List<SupabaseSiteEventDto>>

    @POST("site_events")
    suspend fun postHazard(@Body request: SupabaseSiteEventDto): Response<List<SupabaseSiteEventDto>>

    @PATCH("site_events")
    suspend fun updateHazardStatus(
        @Query("id") idQuery: String,
        @Body statusBody: Map<String, String>
    ): Response<List<SupabaseSiteEventDto>>

    @GET("reports")
    suspend fun getReports(@Query("select") select: String = "*"): Response<List<SupabaseReportDto>>

    @POST("reports")
    suspend fun postReport(@Body request: SupabaseReportDto): Response<List<SupabaseReportDto>>

    @GET("tasks")
    suspend fun getTasks(@Query("select") select: String = "*"): Response<List<SupabaseTaskDto>>

    @POST("tasks")
    suspend fun postTask(@Body request: SupabaseTaskDto): Response<List<SupabaseTaskDto>>

    @PATCH("tasks")
    suspend fun updateTaskStatus(
        @Query("id") idQuery: String,
        @Body statusBody: Map<String, String>
    ): Response<List<SupabaseTaskDto>>

    @GET("blueprints")
    suspend fun getBlueprints(@Query("select") select: String = "*"): Response<List<SupabaseBlueprintDto>>

    @POST("blueprints")
    suspend fun postBlueprint(@Body request: SupabaseBlueprintDto): Response<List<SupabaseBlueprintDto>>

    @GET("devices")
    suspend fun getDevices(@Query("select") select: String = "*"): Response<List<SupabaseDeviceDto>>

    @GET("profiles")
    suspend fun getProfiles(@Query("select") select: String = "*"): Response<List<SupabaseProfileDto>>

    @POST("user_roles")
    suspend fun createUserRole(@Body userRole: SupabaseUserRoleDto): Response<List<SupabaseUserRoleDto>>
}


