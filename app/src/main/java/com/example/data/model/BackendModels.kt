package com.example.data.model

enum class ApiCategory(val displayName: String) {
    ALL("All APIs"),
    AUTH("Auth"),
    PROJECTS("Projects"),
    REPORTS("Reports"),
    DOCUMENTS("Docs & Specs"),
    CAD("CAD / BIM"),
    VISION("Spatial Vision"),
    NOTIFICATIONS("Push & Dispatch"),
    AI("Gemini Multimodal"),
    ADMIN("Admin & Telemetry")
}

data class FastApiEndpoint(
    val id: String,
    val category: ApiCategory,
    val method: String, // GET, POST, PUT, DELETE
    val path: String,
    val summary: String,
    val requestBody: String? = null,
    val sampleResponse: String,
    val isTested: Boolean = false,
    val lastResponseTimeMs: Int = 0
)

data class ContainerServiceStatus(
    val name: String,
    val containerId: String,
    val image: String,
    val status: String,
    val isHealthy: Boolean = true,
    val port: String
)

data class BackendServerStatus(
    val isConnected: Boolean = true,
    val fastapiVersion: String = "v0.111.0",
    val latencyMs: Int = 18,
    val databaseStatus: String = "PostgreSQL 16 (Connected)",
    val redisCacheStatus: String = "Redis 7.0 (Connected)",
    val activeConnections: Int = 1,
    val totalRequestsProcessed: Int = 1420,
    val services: List<ContainerServiceStatus> = listOf(
        ContainerServiceStatus("kaya_fastapi_app", "a8f3b21c", "fastapi:python3.11", "RUNNING", true, "8000:8000"),
        ContainerServiceStatus("kaya_postgres_db", "92c101de", "postgres:16-alpine", "RUNNING", true, "5432:5432"),
        ContainerServiceStatus("kaya_redis_cache", "42f778ab", "redis:7-alpine", "RUNNING", true, "6379:6379")
    )
)

data class BackendConsoleState(
    val selectedCategory: ApiCategory = ApiCategory.ALL,
    val serverStatus: BackendServerStatus = BackendServerStatus(),
    val isTestingApi: Boolean = false,
    val activeTestLog: String? = null,
    val endpointsList: List<FastApiEndpoint> = emptyList()
)
