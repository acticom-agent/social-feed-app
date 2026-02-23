from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime


class AuthorOut(BaseModel):
    id: str
    username: str
    avatarUrl: Optional[str] = None


class UserOut(BaseModel):
    id: str
    username: str
    avatarUrl: Optional[str] = None
    createdAt: datetime


class AuthResponse(BaseModel):
    token: str
    user: UserOut


class CountOut(BaseModel):
    likes: int
    comments: int


class PostListItem(BaseModel):
    id: str
    text: str
    imageUrl: Optional[str] = None
    createdAt: datetime
    authorId: str
    author: AuthorOut
    _count: CountOut

    class Config:
        from_attributes = True


class CommentOut(BaseModel):
    id: str
    text: str
    createdAt: datetime
    postId: str
    authorId: str
    author: AuthorOut


class PostDetail(BaseModel):
    id: str
    text: str
    imageUrl: Optional[str] = None
    createdAt: datetime
    authorId: str
    author: AuthorOut
    comments: List[CommentOut]
    _count: dict
