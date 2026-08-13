from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter(prefix="/notifications", tags=["Dispatch & Push Notifications"])

class NotificationPushRequest(BaseModel):
    category: str
    title: str
    message: str
    priority: str

@router.post("/push")
def push_hud_notification(req: NotificationPushRequest):
    return {
        "status": "DISPATCHED",
        "notification_id": "notif_push_99",
        "hud_chime": "HUD_EARBUD_ALERT_HIGH",
        "delivery_ms": 12
    }

@router.get("/ws-status")
def get_websocket_connection_status():
    return {
        "active_hud_connections": 1,
        "glasses_device": "Ray-Ban Meta Smart Glasses (Gen 2)",
        "websocket_protocol": "WSS_TLS1_3",
        "status": "HEALTHY"
    }
