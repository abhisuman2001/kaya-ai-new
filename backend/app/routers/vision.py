from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter(prefix="/vision", tags=["Spatial AI Vision"])

class VisionDetection(BaseModel):
    id: str
    objectClass: str
    confidence: float
    bbox: List[float]
    safetyStatus: str

@router.post("/analyze-stream")
def analyze_glasses_frame(frame_id: str):
    return {
        "frame_id": frame_id,
        "processed_fps": 30.0,
        "detections": [
            {
                "id": "det_ppe_01",
                "objectClass": "HardHat",
                "confidence": 0.99,
                "bbox": [100.0, 150.0, 200.0, 250.0],
                "safetyStatus": "COMPLIANT"
            },
            {
                "id": "det_rebar_02",
                "objectClass": "Rebar Spacing Grid",
                "confidence": 0.94,
                "bbox": [300.0, 400.0, 600.0, 700.0],
                "safetyStatus": "DEVIATION_DETECTED_+18MM"
            }
        ]
    }
