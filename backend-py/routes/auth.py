from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from models import User
from auth import hash_password, verify_password, generate_token, get_current_user_id

router = APIRouter(prefix="/api/auth")


def _user_json(user):
    ca = user.created_at.isoformat() + "Z" if user.created_at else None
    return {
        "id": user.id,
        "username": user.username,
        "avatarUrl": user.avatar_url,
        "createdAt": ca,
    }


@router.post("/register", status_code=201)
def register(body: dict, db: Session = Depends(get_db)):
    username = body.get("username")
    password = body.get("password")
    if not username or not password:
        raise HTTPException(400, "Username and password required")
    if len(password) < 4:
        raise HTTPException(400, "Password must be at least 4 characters")
    if db.query(User).filter(User.username == username).first():
        raise HTTPException(409, "Username already taken")
    user = User(username=username, password_hash=hash_password(password))
    db.add(user)
    db.commit()
    db.refresh(user)
    return {"token": generate_token(user.id), "user": _user_json(user)}


@router.post("/login")
def login(body: dict, db: Session = Depends(get_db)):
    username = body.get("username")
    password = body.get("password")
    if not username or not password:
        raise HTTPException(400, "Username and password required")
    user = db.query(User).filter(User.username == username).first()
    if not user or not verify_password(password, user.password_hash):
        raise HTTPException(401, "Invalid credentials")
    return {"token": generate_token(user.id), "user": _user_json(user)}


@router.get("/me")
def me(db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(404, "User not found")
    return _user_json(user)
