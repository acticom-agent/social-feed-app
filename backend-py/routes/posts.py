import os
import uuid
import shutil
from fastapi import APIRouter, Depends, HTTPException, Request, UploadFile, File, Form
from sqlalchemy.orm import Session
from sqlalchemy import func
from database import get_db
from models import Post, Like, Comment, User
from auth import get_current_user_id, get_optional_user_id

router = APIRouter(prefix="/api/posts")

UPLOAD_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)

ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"}


def _post_to_dict(post, likes_count, comments_count):
    return {
        "id": post.id,
        "text": post.text,
        "imageUrl": post.image_url,
        "createdAt": post.created_at.isoformat() + "Z" if post.created_at else None,
        "authorId": post.author_id,
        "author": {
            "id": post.author.id,
            "username": post.author.username,
            "avatarUrl": post.author.avatar_url,
        },
        "_count": {"likes": likes_count, "comments": comments_count},
    }


def _comment_to_dict(c):
    return {
        "id": c.id,
        "text": c.text,
        "createdAt": c.created_at.isoformat() + "Z" if c.created_at else None,
        "postId": c.post_id,
        "authorId": c.author_id,
        "author": {
            "id": c.author.id,
            "username": c.author.username,
            "avatarUrl": c.author.avatar_url,
        },
    }


@router.get("/")
def list_posts(request: Request, limit: int = 20, offset: int = 0, db: Session = Depends(get_db)):
    limit = min(limit, 100)
    posts = db.query(Post).order_by(Post.created_at.desc()).offset(offset).limit(limit).all()
    total = db.query(func.count(Post.id)).scalar()
    result = []
    for p in posts:
        lc = db.query(func.count(Like.id)).filter(Like.post_id == p.id).scalar()
        cc = db.query(func.count(Comment.id)).filter(Comment.post_id == p.id).scalar()
        result.append(_post_to_dict(p, lc, cc))
    return {"posts": result, "total": total, "limit": limit, "offset": offset}


@router.get("/{post_id}")
def get_post(post_id: str, db: Session = Depends(get_db)):
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(404, "Post not found")
    lc = db.query(func.count(Like.id)).filter(Like.post_id == post.id).scalar()
    comments = db.query(Comment).filter(Comment.post_id == post_id).order_by(Comment.created_at.asc()).all()
    return {
        "id": post.id,
        "text": post.text,
        "imageUrl": post.image_url,
        "createdAt": post.created_at.isoformat() + "Z" if post.created_at else None,
        "authorId": post.author_id,
        "author": {
            "id": post.author.id,
            "username": post.author.username,
            "avatarUrl": post.author.avatar_url,
        },
        "comments": [_comment_to_dict(c) for c in comments],
        "_count": {"likes": lc},
    }


@router.post("/", status_code=201)
async def create_post(
    text: str = Form(...),
    image: UploadFile = File(None),
    db: Session = Depends(get_db),
    user_id: str = Depends(get_current_user_id),
):
    if not text or not text.strip():
        raise HTTPException(400, "Text is required")
    image_url = None
    if image and image.filename:
        ext = os.path.splitext(image.filename)[1].lower()
        if ext not in ALLOWED_EXTENSIONS:
            raise HTTPException(400, "Only image files are allowed")
        filename = f"{uuid.uuid4()}{ext}"
        path = os.path.join(UPLOAD_DIR, filename)
        with open(path, "wb") as f:
            content = await image.read()
            f.write(content)
        image_url = f"/uploads/{filename}"

    post = Post(text=text.strip(), image_url=image_url, author_id=user_id)
    db.add(post)
    db.commit()
    db.refresh(post)
    return {
        "id": post.id,
        "text": post.text,
        "imageUrl": post.image_url,
        "createdAt": post.created_at.isoformat() + "Z" if post.created_at else None,
        "authorId": post.author_id,
        "author": {
            "id": post.author.id,
            "username": post.author.username,
            "avatarUrl": post.author.avatar_url,
        },
    }


@router.delete("/{post_id}")
def delete_post(post_id: str, db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(404, "Post not found")
    if post.author_id != user_id:
        raise HTTPException(403, "Not authorized")
    db.delete(post)
    db.commit()
    return {"message": "Post deleted"}


@router.post("/{post_id}/like")
def toggle_like(post_id: str, db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(404, "Post not found")
    existing = db.query(Like).filter(Like.post_id == post_id, Like.user_id == user_id).first()
    if existing:
        db.delete(existing)
        db.commit()
        return {"liked": False}
    like = Like(post_id=post_id, user_id=user_id)
    db.add(like)
    db.commit()
    return {"liked": True}


@router.get("/{post_id}/likes")
def get_likes(post_id: str, request: Request, db: Session = Depends(get_db)):
    count = db.query(func.count(Like.id)).filter(Like.post_id == post_id).scalar()
    liked = False
    user_id = get_optional_user_id(request)
    if user_id:
        existing = db.query(Like).filter(Like.post_id == post_id, Like.user_id == user_id).first()
        liked = existing is not None
    return {"count": count, "liked": liked}


@router.get("/{post_id}/comments")
def get_comments(post_id: str, limit: int = 20, offset: int = 0, db: Session = Depends(get_db)):
    limit = min(limit, 100)
    comments = db.query(Comment).filter(Comment.post_id == post_id).order_by(Comment.created_at.asc()).offset(offset).limit(limit).all()
    total = db.query(func.count(Comment.id)).filter(Comment.post_id == post_id).scalar()
    return {"comments": [_comment_to_dict(c) for c in comments], "total": total, "limit": limit, "offset": offset}


@router.post("/{post_id}/comments", status_code=201)
def create_comment(post_id: str, body: dict, db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    text = body.get("text")
    if not text or not text.strip():
        raise HTTPException(400, "Text is required")
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(404, "Post not found")
    comment = Comment(text=text.strip(), post_id=post_id, author_id=user_id)
    db.add(comment)
    db.commit()
    db.refresh(comment)
    return _comment_to_dict(comment)
