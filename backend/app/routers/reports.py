from fastapi import APIRouter, Depends
from pydantic import BaseModel
from typing import List, Optional
from sqlalchemy.orm import Session
import uuid
import datetime
from app.db.session import get_db
from app.models.models import Report

router = APIRouter(prefix="/reports", tags=["Reports"])

class ReportCreate(BaseModel):
    title: str
    shift_type: Optional[str] = "DAY"
    summary: Optional[str] = ""
    crew_count: Optional[int] = 18
    hazards_found: Optional[int] = 0
    project_id: Optional[str] = "proj_01"

@router.get("")
def get_site_reports(db: Session = Depends(get_db)):
    reports = db.query(Report).all()
    if not reports:
        return [
            {
                "id": "report_dpr_42",
                "title": "Level 18 Deck Pour DPR",
                "shift_type": "DAY",
                "summary": "Level 18 Deck Pour completed with 18 field snapshots & rebar audit score of 98%.",
                "crew_count": 18,
                "hazards_found": 1,
                "status": "SUBMITTED",
                "timestamp": datetime.datetime.now().isoformat()
            }
        ]
    return [
        {
            "id": r.id,
            "title": r.title,
            "shift_type": r.shift_type,
            "summary": r.summary,
            "crew_count": r.crew_count,
            "hazards_found": r.hazards_found,
            "status": r.status,
            "timestamp": r.timestamp
        }
        for r in reports
    ]

@router.post("")
def create_site_report(report: ReportCreate, db: Session = Depends(get_db)):
    r_id = f"rpt_{uuid.uuid4().hex[:6]}"
    new_report = Report(
        id=r_id,
        project_id=report.project_id or "proj_01",
        title=report.title,
        shift_type=report.shift_type or "DAY",
        summary=report.summary or "",
        crew_count=report.crew_count or 18,
        hazards_found=report.hazards_found or 0,
        timestamp=datetime.datetime.now().isoformat(),
        status="SUBMITTED"
    )
    db.add(new_report)
    db.commit()
    db.refresh(new_report)
    return {
        "id": new_report.id,
        "title": new_report.title,
        "shift_type": new_report.shift_type,
        "summary": new_report.summary,
        "crew_count": new_report.crew_count,
        "hazards_found": new_report.hazards_found,
        "status": new_report.status,
        "timestamp": new_report.timestamp
    }
