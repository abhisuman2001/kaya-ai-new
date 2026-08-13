from fastapi import APIRouter, HTTPException, Depends, status
from pydantic import BaseModel
from typing import List, Optional
from sqlalchemy.orm import Session
import uuid
import datetime
from app.db.session import get_db
from app.models.models import Project, Hazard

router = APIRouter(prefix="", tags=["Projects & Hazards"])

class HazardCreate(BaseModel):
    title: str
    category: str
    severity: str
    location: str
    osha_standard: Optional[str] = "OSHA 1926 General Safety"
    description: Optional[str] = ""
    assigned_worker_id: Optional[str] = None
    assigned_worker_name: Optional[str] = None
    project_id: Optional[str] = "proj_01"

class ProjectCreate(BaseModel):
    name: str
    location: str
    description: Optional[str] = ""

@router.get("/projects")
def list_projects(db: Session = Depends(get_db)):
    projects = db.query(Project).all()
    if not projects:
        return [
            {
                "id": "proj_01",
                "name": "Metro Tower Construction",
                "location": "Downtown Financial District, Bay Area",
                "description": "Level 18 Deck Pour & Active Site",
                "progressPct": 68.5,
                "activeWorkers": 42
            }
        ]
    return [
        {
            "id": p.id,
            "name": p.name,
            "location": p.location,
            "description": p.description,
            "progressPct": 68.5,
            "activeWorkers": 42
        }
        for p in projects
    ]

@router.post("/projects")
def create_project(proj: ProjectCreate, db: Session = Depends(get_db)):
    p_id = f"proj_{uuid.uuid4().hex[:6]}"
    new_proj = Project(
        id=p_id,
        name=proj.name,
        location=proj.location,
        description=proj.description
    )
    db.add(new_proj)
    db.commit()
    db.refresh(new_proj)
    return {
        "id": new_proj.id,
        "name": new_proj.name,
        "location": new_proj.location,
        "description": new_proj.description
    }

@router.get("/hazards")
@router.get("/projects/{project_id}/hazards")
def list_hazards(project_id: Optional[str] = None, db: Session = Depends(get_db)):
    query = db.query(Hazard)
    if project_id:
        query = query.filter(Hazard.project_id == project_id)
    hazards = query.all()
    return [
        {
            "id": h.id,
            "project_id": h.project_id,
            "title": h.title,
            "category": h.category,
            "severity": h.severity,
            "location": h.location,
            "osha_standard": h.osha_standard,
            "description": h.description,
            "assigned_worker_id": h.assigned_worker_id,
            "assigned_worker_name": h.assigned_worker_name,
            "is_resolved": h.is_resolved,
            "timestamp": h.timestamp
        }
        for h in hazards
    ]

@router.post("/hazards")
@router.post("/projects/{project_id}/hazards")
def create_hazard(hazard: HazardCreate, project_id: Optional[str] = None, db: Session = Depends(get_db)):
    h_id = f"hz_{uuid.uuid4().hex[:6]}"
    p_id = project_id or hazard.project_id or "proj_01"
    new_hazard = Hazard(
        id=h_id,
        project_id=p_id,
        title=hazard.title,
        category=hazard.category,
        severity=hazard.severity,
        location=hazard.location,
        osha_standard=hazard.osha_standard or "OSHA 1926",
        description=hazard.description or "",
        assigned_worker_id=hazard.assigned_worker_id,
        assigned_worker_name=hazard.assigned_worker_name,
        is_resolved=False,
        timestamp=datetime.datetime.now().isoformat()
    )
    db.add(new_hazard)
    db.commit()
    db.refresh(new_hazard)
    return {
        "id": new_hazard.id,
        "project_id": new_hazard.project_id,
        "title": new_hazard.title,
        "category": new_hazard.category,
        "severity": new_hazard.severity,
        "location": new_hazard.location,
        "osha_standard": new_hazard.osha_standard,
        "description": new_hazard.description,
        "assigned_worker_id": new_hazard.assigned_worker_id,
        "assigned_worker_name": new_hazard.assigned_worker_name,
        "is_resolved": new_hazard.is_resolved,
        "timestamp": new_hazard.timestamp,
        "status": "CREATED"
    }
