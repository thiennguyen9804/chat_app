from typing import Any
import jwt
from fastapi import HTTPException, Depends
from fastapi.security import  HTTPAuthorizationCredentials, HTTPBearer
from config import settings

security: HTTPBearer = HTTPBearer()

async def verify_token(token: HTTPAuthorizationCredentials | str = Depends(security)) -> int:
    if type(token) is HTTPAuthorizationCredentials:
        try:
            payload: dict[str, Any] = jwt.decode( # pyright: ignore[reportUnknownMemberType]
                jwt=token.credentials,
                key=settings.JWT_SECRET,
                algorithms=[settings.JWT_ALGORITHM],
            )

            user_id: int | None = int(payload['userId'])
            return user_id
        except ValueError:
            raise HTTPException(status_code=401, detail="Invalid token payload")
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="Could not validate credentials")
    elif type(token) is str:
        try:
            payload: dict[str, Any] = jwt.decode( # pyright: ignore[reportUnknownMemberType]
                jwt=token,
                key=settings.JWT_SECRET,
                algorithms=[settings.JWT_ALGORITHM],
            )
            user_id: int | None = int(payload['userId'])
            return user_id
        except ValueError:
            raise HTTPException(status_code=401, detail="Invalid token payload")
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="Could not validate credentials")
    else:
        raise HTTPException(status_code=401, detail="Invalid token payload")
    