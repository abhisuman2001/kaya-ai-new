from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter(prefix="/cad", tags=["CAD & BIM Engines"])

class ClashResult(BaseModel):
    clashId: str
    tradeA: str
    tradeB: str
    location: str
    severity: str
    toleranceMm: float

@router.get("/clash-detection", response_model=List[ClashResult])
def run_clash_detection():
    return [
        ClashResult(
            clashId="CLASH_108",
            tradeA="HVAC Ducting",
            tradeB="Structural Steel I-Beam",
            location="Grid B-4 Level 18 Ceiling",
            severity="HIGH",
            toleranceMm=42.0
        ),
        ClashResult(
            clashId="CLASH_109",
            tradeA="Plumbing Riser Pipe",
            tradeB="Electrical Cable Tray",
            location="Grid C-2 West Riser",
            severity="MEDIUM",
            toleranceMm=18.5
        )
    ]

@router.post("/parse-ifc")
def parse_ifc_file(filename: str):
    return {
        "filename": filename,
        "ifc_elements_count": 1420,
        "slabs_count": 18,
        "columns_count": 84,
        "status": "PARSED_SUCCESS"
    }
