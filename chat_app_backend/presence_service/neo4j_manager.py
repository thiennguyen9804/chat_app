from neo4j import AsyncGraphDatabase, AsyncDriver
from config import settings

class Neo4jManager():
    def __init__(self) -> None:
        self.driver: AsyncDriver | None = None

    async def connect(self):
        self.driver = AsyncGraphDatabase.driver( # type: ignore
            uri=settings.NEO4J_URI,
            auth=settings.AUTH,
        )

    async def close(self):
        if self.driver:
            await self.driver.close()

    async def get_db(self):
        return self.driver
    
db_manager = Neo4jManager()
