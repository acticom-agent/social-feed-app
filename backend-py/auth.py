import os
from datetime import datetime, timedelta, timezone
from jose import jwt, JWTError
import bcrypt as _bcrypt
from fastapi import Request, HTTPException

JWT_SECRET = os.getenv("JWT_SECRET", "dev-secret")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_DAYS = 7


def hash_password(password: str) -> str:
    return _bcrypt.hashpw(password.encode("utf-8"), _bcrypt.gensalt()).decode("utf-8")


def verify_password(plain: str, hashed: str) -> bool:
    return _bcrypt.checkpw(plain.encode("utf-8"), hashed.encode("utf-8"))


def generate_token(user_id: str) -> str:
    expire = datetime.now(timezone.utc) + timedelta(days=ACCESS_TOKEN_EXPIRE_DAYS)
    return jwt.encode({"userId": user_id, "exp": expire}, JWT_SECRET, algorithm=ALGORITHM)


def decode_token(token: str) -> str:
    payload = jwt.decode(token, JWT_SECRET, algorithms=[ALGORITHM])
    return payload["userId"]


def get_current_user_id(request: Request) -> str:
    auth = request.headers.get("authorization", "")
    if not auth.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="No token provided")
    try:
        return decode_token(auth[7:])
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")


def get_optional_user_id(request: Request):
    auth = request.headers.get("authorization", "")
    if auth.startswith("Bearer "):
        try:
            return decode_token(auth[7:])
        except JWTError:
            pass
    return None
