from fastapi import APIRouter, HTTPException, Depends, status
from pydantic import BaseModel, EmailStr
from typing import Optional
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.models.models import User
from app.core.security import create_access_token, verify_password, get_password_hash

router = APIRouter(prefix="/auth", tags=["Authentication"])

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class RegisterRequest(BaseModel):
    name: str
    email: EmailStr
    password: str
    company: Optional[str] = "BuildTech Global"
    role: Optional[str] = "SUPERVISOR"
    job_title: Optional[str] = "Safety Inspector"

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: str
    name: str
    role: str

@router.post("/login", response_model=TokenResponse)
def login(request: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == request.email).first()
    if not user or not verify_password(request.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password"
        )
    
    token = create_access_token(subject=user.id)
    return TokenResponse(
        access_token=token,
        user_id=user.id,
        name=user.name,
        role=user.role
    )

@router.post("/register", response_model=TokenResponse)
def register(request: RegisterRequest, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.email == request.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="User with this email already exists"
        )
    
    user_id = f"user_{request.email.split('@')[0]}"
    new_user = User(
        id=user_id,
        email=request.email,
        hashed_password=get_password_hash(request.password),
        name=request.name,
        role=request.role,
        job_title=request.job_title,
        company=request.company
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    token = create_access_token(subject=new_user.id)
    return TokenResponse(
        access_token=token,
        user_id=new_user.id,
        name=new_user.name,
        role=new_user.role
    )

class UserProfileUpdateRequest(BaseModel):
    user_id: str
    name: Optional[str] = None
    email: Optional[str] = None
    role: Optional[str] = None
    job_title: Optional[str] = None
    company: Optional[str] = None
    site_location: Optional[str] = None
    connected_glasses_model: Optional[str] = None
    theme: Optional[str] = None
    language: Optional[str] = None
    avatar_url: Optional[str] = None

@router.get("/me")
def get_current_user(user_id: Optional[str] = None, email: Optional[str] = None, db: Session = Depends(get_db)):
    user = None
    if user_id:
        user = db.query(User).filter(User.id == user_id).first()
    if not user and email:
        user = db.query(User).filter(User.email == email).first()
    if not user:
        user = db.query(User).first()

    if user:
        return {
            "id": user.id,
            "name": user.name,
            "email": user.email,
            "role": user.role,
            "job_title": user.job_title or "Site Inspector",
            "company": user.company or "BuildTech Global",
            "site_location": getattr(user, "site_location", "Metro Tower Construction — Active Site"),
            "connected_glasses_model": getattr(user, "connected_glasses_model", "Ray-Ban Meta Smart Glasses (Gen 2)"),
            "theme": getattr(user, "theme", "Dark Mode"),
            "language": getattr(user, "language", "English (US)"),
            "avatar_url": getattr(user, "avatar_url", ""),
            "is_google_auth": getattr(user, "is_google_auth", False)
        }
    return {
        "id": "user_101",
        "name": "Marcus Vance",
        "email": "marcus.vance@sitemind.ai",
        "role": "SUPERVISOR",
        "job_title": "Senior Safety Inspector",
        "company": "BuildTech Global Engineering",
        "site_location": "Metro Tower Construction — Active Site",
        "connected_glasses_model": "Ray-Ban Meta Smart Glasses (Gen 2)",
        "theme": "Dark Mode",
        "language": "English (US)",
        "avatar_url": "",
        "is_google_auth": False
    }

@router.post("/profile")
def update_user_profile(request: UserProfileUpdateRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == request.user_id).first()
    if not user and request.email:
        user = db.query(User).filter(User.email == request.email).first()

    if not user:
        user_id = request.user_id if request.user_id else f"user_{System.currentTimeMillis()}"
        user = User(
            id=user_id,
            email=request.email or "user@sitemind.ai",
            hashed_password=get_password_hash("defaultpass"),
            name=request.name or "Site Engineer",
            role=request.role or "SUPERVISOR",
            job_title=request.job_title,
            company=request.company
        )
        db.add(user)

    if request.name: user.name = request.name
    if request.email: user.email = request.email
    if request.role: user.role = request.role
    if request.job_title: user.job_title = request.job_title
    if request.company: user.company = request.company
    if request.site_location: user.site_location = request.site_location
    if request.connected_glasses_model: user.connected_glasses_model = request.connected_glasses_model
    if request.theme: user.theme = request.theme
    if request.language: user.language = request.language
    if request.avatar_url: user.avatar_url = request.avatar_url

    db.commit()
    db.refresh(user)

    return {
        "status": "success",
        "message": "User details updated in backend database",
        "user": {
            "id": user.id,
            "name": user.name,
            "email": user.email,
            "role": user.role,
            "job_title": user.job_title,
            "company": user.company,
            "site_location": getattr(user, "site_location", ""),
            "theme": getattr(user, "theme", "Dark Mode")
        }
    }
