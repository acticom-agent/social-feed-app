from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from models import User
from auth import get_current_user_id

router = APIRouter(prefix="/api/users")


def _user_json(user):
    return {
        "id": user.id,
        "username": user.username,
        "avatarUrl": user.avatar_url,
        "createdAt": user.created_at.isoformat() + "Z" if user.created_at else None,
    }


@router.get("/{user_id}")
def get_user(user_id: str, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(404, "User not found")
    return _user_json(user)


@router.put("/me")
def update_me(body: dict, db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    user = db.query(User).filter(User.id == user_id).first()
    username = body.get("username")
    avatar_url = body.get("avatarUrl")
    if username is not None:
        if not username:
            raise HTTPException(400, "Username cannot be empty")
        existing = db.query(User).filter(User.username == username).first()
        if existing and existing.id != user_id:
            raise HTTPException(409, "Username already taken")
        user.username = username
    if avatar_url is not None:
        user.avatar_url = avatar_url
    db.commit()
    db.refresh(user)
    return _user_json(user)
