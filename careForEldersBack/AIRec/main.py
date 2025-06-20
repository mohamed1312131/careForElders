from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from sentence_transformers import SentenceTransformer, util

app = FastAPI()
model = SentenceTransformer('all-MiniLM-L6-v2')

class RecommendationRequest(BaseModel):
    user_summary: str
    program_summaries: List[str]

@app.post("/recommend")
async def recommend(data: RecommendationRequest):
    user_embedding = model.encode(data.user_summary, convert_to_tensor=True)
    program_embeddings = model.encode(data.program_summaries, convert_to_tensor=True)

    scores = util.cos_sim(user_embedding, program_embeddings)[0]
    top_indices = scores.argsort(descending=True)[:5]

    return [data.program_summaries[i] for i in top_indices]
