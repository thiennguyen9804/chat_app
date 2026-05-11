from contextlib import asynccontextmanager
from fastapi import FastAPI, APIRouter, WebSocket, status, Query
import redis.asyncio as redis
from auth import verify_token
from config import settings
from neo4j_manager import db_manager

@asynccontextmanager
async def lifespan(app: FastAPI):
    # --- Startup: Load resources (e.g., DB connection, ML models) ---
    await db_manager.connect()
    yield
    # --- Shutdown: Clean up resources ---
    await db_manager.close()

app: FastAPI = FastAPI(title="Presence Servive", lifespan=lifespan)

api_router: APIRouter = APIRouter(prefix="/api")

r: redis.Redis = redis.from_url(
    url=settings.REDIS_URL,
    decode_responses=True,
    retry_on_timeout=True
)

@app.websocket("/ws/ping")
async def websocket_heartbeat(websocket: WebSocket, token: str = Query(...)):
    user_id: int = await verify_token(token)
    if not user_id:
        await websocket.close(code=status.WS_1008_POLICY_VIOLATION)
        return

    await websocket.accept()
    while True:
        data = await websocket.receive_text()
        if data == "ping":
            await r.setex(f"user:{user_id}:status", 30, "online")


app.include_router(api_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8082, reload=True)