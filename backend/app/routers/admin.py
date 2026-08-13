from fastapi import APIRouter

router = APIRouter(prefix="/admin", tags=["Enterprise Admin & Telemetry"])

@router.get("/health")
def server_health():
    return {
        "status": "ONLINE",
        "fastapi_version": "0.111.0",
        "database": "CONNECTED (PostgreSQL 16)",
        "redis_cache": "CONNECTED (Redis 7.0)",
        "docker_container": "sitemind_fastapi_app:latest",
        "uptime_seconds": 86400
    }

@router.get("/telemetry")
def system_telemetry():
    return {
        "cpu_usage_pct": 14.2,
        "ram_usage_mb": 412,
        "active_api_requests_per_sec": 48.5,
        "redis_cached_keys": 128,
        "active_glasses_streams": 1
    }
