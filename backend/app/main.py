from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.db.session import engine, Base, SessionLocal
from app.models.models import User, Project
from app.core.security import get_password_hash
from app.routers import (
    auth,
    projects,
    reports,
    documents,
    cad,
    vision,
    notifications,
    ai,
    admin
)

# Create database tables automatically
Base.metadata.create_all(bind=engine)

# Seed initial default supervisor user and default project
def seed_initial_data():
    db = SessionLocal()
    try:
        if not db.query(User).filter(User.email == "marcus.vance@sitemind.ai").first():
            supervisor = User(
                id="user_101",
                email="marcus.vance@sitemind.ai",
                hashed_password=get_password_hash("sitemind2026"),
                name="Marcus Vance",
                role="SUPERVISOR",
                job_title="Senior Safety Inspector & Superintendent",
                company="BuildTech Global Engineering"
            )
            db.add(supervisor)
        
        if not db.query(User).filter(User.email == "carlos.rodriguez@sitemind.ai").first():
            worker = User(
                id="w_101",
                email="carlos.rodriguez@sitemind.ai",
                hashed_password=get_password_hash("sitemind2026"),
                name="Carlos Rodriguez",
                role="WORKER",
                job_title="Ironworker",
                company="BuildTech Global Engineering"
            )
            db.add(worker)

        if not db.query(Project).filter(Project.id == "proj_01").first():
            proj = Project(
                id="proj_01",
                name="Metro Tower Construction",
                location="Downtown Financial District, Bay Area",
                description="Level 18 Deck Pour & Active Site"
            )
            db.add(proj)

        db.commit()
    except Exception as e:
        print(f"Error seeding data: {e}")
    finally:
        db.close()

seed_initial_data()

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="Complete Enterprise FastAPI Backend for SiteMind AI — Smart Glasses Construction Platform.",
    openapi_url=f"{settings.API_V1_STR}/openapi.json",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS Middleware setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include all modular API routers
app.include_router(auth.router, prefix=settings.API_V1_STR)
app.include_router(projects.router, prefix=settings.API_V1_STR)
app.include_router(reports.router, prefix=settings.API_V1_STR)
app.include_router(documents.router, prefix=settings.API_V1_STR)
app.include_router(cad.router, prefix=settings.API_V1_STR)
app.include_router(vision.router, prefix=settings.API_V1_STR)
app.include_router(notifications.router, prefix=settings.API_V1_STR)
app.include_router(ai.router, prefix=settings.API_V1_STR)
app.include_router(admin.router, prefix=settings.API_V1_STR)

@app.get("/")
def root():
    return {
        "message": "Welcome to SiteMind AI FastAPI Enterprise Backend",
        "docs_url": "/docs",
        "health": f"{settings.API_V1_STR}/admin/health"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
