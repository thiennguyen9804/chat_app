from pydantic_settings import BaseSettings, SettingsConfigDict
from functools import lru_cache
import os
from neo4j import Auth, basic_auth

class Settings(BaseSettings):
    APP_NAME: str = "Presence Service"
    REDIS_URL: str = "redis://localhost:6379"
    JWT_SECRET: str = "your-very-secure-secret-key-must-be-long-enough"
    JWT_ALGORITHM: str = "HS256"
    DEBUG: bool = True
    model_config = SettingsConfigDict(env_file=".env")
    NEO4J_URI = "bolt://localhost:7687"
    # AUTH: Auth = ("neo4j", "password123")   
    AUTH: Auth = basic_auth("neo4j", "password123")

class DockerSettings(Settings):
    REDIS_URL: str = "redis://redis-local:6379"
    NEO4J_URI = "bolt://neo4j-local:7687"

@lru_cache
def get_settings():
    env: str = os.getenv("APP_ENV", "").lower()
    if env == "docker":
        return DockerSettings()
    
    return Settings()

settings = get_settings()
