from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from models import Comment
from auth import get_current_user_id

router = APIRouter(prefix="/api/comments")


@router.delete("/{comment_id}")
def delete_comment(comment_id: str, db: Session = Depends(get_db), user_id: str = Depends(get_current_user_id)):
    comment = db.query(Comment).filter(Comment.id == comment_id).first()
    if not comment:
        raise HTTPException(404, "Comment not found")
    if comment.author_id != user_id:
        raise HTTPException(403, "Not authorized")
    db.delete(comment)
    db.commit()
    return {"message": "Comment deleted"}
