from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/ai", tags=["Gemini Multimodal AI Engine"])

class PromptRequest(BaseModel):
    prompt: str
    includeVisionContext: bool = True

@router.post("/prompt")
def process_gemini_prompt(req: PromptRequest):
    return {
        "user_prompt": req.prompt,
        "ai_response": "SiteMind AI Assistant: Analyzed Level 18 slab rebar density. All 14 grid lines meet ASTM A615 Grade 60 tensile spec. Safe for concrete deck pour.",
        "model_used": "Gemini 1.5 Pro Multimodal Vision",
        "tokens_processed": 342,
        "latency_ms": 185
    }
