# embedding-service/main.py
from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

logger.info("Loading embedding model...")
# Load the embedding model (this might take time on first run)
model = SentenceTransformer('all-MiniLM-L6-v2')
logger.info("Embedding model loaded successfully.")


# Create FastAPI app
app = FastAPI()

# Define Request Body
class EmbeddingRequest(BaseModel):
    text: str

# Define Response Body
class EmbeddingResponse(BaseModel):
    embedding: list[float] # More specific type hint

# Define Endpoint
@app.post("/embed", response_model=EmbeddingResponse)
async def embed(request: EmbeddingRequest):
    logger.info(f"Received embedding request for text: {request.text[:50]}...") # Log snippet
    text = request.text
    vector = model.encode(text).tolist()
    logger.info(f"Generated embedding with dimension: {len(vector)}")
    return EmbeddingResponse(embedding=vector)

@app.get("/health")
async def health_check():
    # Basic health check endpoint
    return {"status": "ok"}

# Optional: Add main block for direct execution if needed (uvicorn handles this)
# if __name__ == "__main__":
#     import uvicorn
#     uvicorn.run(app, host="0.0.0.0", port=8000)