from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter(prefix="/documents", tags=["Documents & Specifications"])

class DocumentItem(BaseModel):
    id: str
    code: str
    title: str
    category: str
    revision: str
    fileSizeMb: float

@router.get("", response_model=List[DocumentItem])
def list_documents():
    return [
        DocumentItem(
            id="doc_spec_01",
            code="SOP-OSHA-2026",
            title="OSHA Fall Protection & Safety Harness Guidelines",
            category="Safety SOP",
            revision="v4.2",
            fileSizeMb=2.4
        ),
        DocumentItem(
            id="doc_spec_02",
            code="SPEC-CONCRETE-301",
            title="High-Strength Reinforced Deck Pour Specifications",
            category="Engineering Spec",
            revision="v2.1",
            fileSizeMb=5.1
        )
    ]

@router.post("/rag-search")
def rag_search_documents(query: str):
    return {
        "query": query,
        "matched_chunks": [
            {
                "doc": "SOP-OSHA-2026",
                "text": "100% tie-off mandatory for all personnel working within 6ft of unguarded slab edges at heights above 10ft.",
                "relevance_score": 0.96
            }
        ]
    }
