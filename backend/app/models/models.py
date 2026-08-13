from sqlalchemy import Column, String, Integer, Boolean, Text, DateTime, ForeignKey
from sqlalchemy.orm import relationship
import datetime
from app.db.session import Base

class User(Base):
    __tablename__ = "users"

    id = Column(String, primary_key=True, index=True)
    email = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)
    name = Column(String, nullable=False)
    role = Column(String, default="SUPERVISOR")
    job_title = Column(String, nullable=True)
    company = Column(String, nullable=True)
    site_location = Column(String, default="Metro Tower Construction — Active Site")
    connected_glasses_model = Column(String, default="Ray-Ban Meta Smart Glasses (Gen 2)")
    theme = Column(String, default="Dark Mode")
    language = Column(String, default="English (US)")
    avatar_url = Column(String, nullable=True)
    is_google_auth = Column(Boolean, default=False)

class Project(Base):
    __tablename__ = "projects"

    id = Column(String, primary_key=True, index=True)
    name = Column(String, nullable=False)
    description = Column(String, nullable=True)
    location = Column(String, nullable=True)

class Hazard(Base):
    __tablename__ = "hazards"

    id = Column(String, primary_key=True, index=True)
    project_id = Column(String, ForeignKey("projects.id"), nullable=True)
    title = Column(String, nullable=False)
    category = Column(String, nullable=False)
    severity = Column(String, nullable=False)
    location = Column(String, nullable=False)
    osha_standard = Column(String, nullable=True)
    description = Column(Text, nullable=True)
    assigned_worker_id = Column(String, nullable=True)
    assigned_worker_name = Column(String, nullable=True)
    is_resolved = Column(Boolean, default=False)
    timestamp = Column(String, nullable=True)

class Report(Base):
    __tablename__ = "reports"

    id = Column(String, primary_key=True, index=True)
    project_id = Column(String, ForeignKey("projects.id"), nullable=True)
    title = Column(String, nullable=False)
    shift_type = Column(String, default="DAY")
    summary = Column(Text, nullable=True)
    crew_count = Column(Integer, default=0)
    hazards_found = Column(Integer, default=0)
    timestamp = Column(String, nullable=True)
    status = Column(String, default="SUBMITTED")

class Blueprint(Base):
    __tablename__ = "blueprints"

    id = Column(String, primary_key=True, index=True)
    project_id = Column(String, ForeignKey("projects.id"), nullable=True)
    name = Column(String, nullable=False)
    sheet_number = Column(String, nullable=True)
    revision = Column(String, default="Rev 1.0")
    url = Column(String, nullable=True)
