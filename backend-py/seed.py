from database import engine, SessionLocal, Base
from models import User, Post, Like, Comment
from auth import hash_password

Base.metadata.create_all(bind=engine)

db = SessionLocal()

# Clear
db.query(Comment).delete()
db.query(Like).delete()
db.query(Post).delete()
db.query(User).delete()
db.commit()

pw = hash_password("password")

alice = User(username="alice", password_hash=pw, avatar_url="https://i.pravatar.cc/150?u=alice")
bob = User(username="bob", password_hash=pw, avatar_url="https://i.pravatar.cc/150?u=bob")
charlie = User(username="charlie", password_hash=pw, avatar_url="https://i.pravatar.cc/150?u=charlie")
db.add_all([alice, bob, charlie])
db.commit()
db.refresh(alice); db.refresh(bob); db.refresh(charlie)

post1 = Post(text="Hello world! This is my first post 🌍", author_id=alice.id)
post2 = Post(text="Beautiful sunset today 🌅", image_url="https://picsum.photos/seed/sunset/800/600", author_id=bob.id)
post3 = Post(text="Just shipped a new feature! 🚀", author_id=charlie.id)
db.add_all([post1, post2, post3])
db.commit()
db.refresh(post1); db.refresh(post2); db.refresh(post3)

db.add_all([
    Like(post_id=post1.id, user_id=bob.id),
    Like(post_id=post1.id, user_id=charlie.id),
    Like(post_id=post2.id, user_id=alice.id),
])
db.commit()

db.add_all([
    Comment(text="Welcome! 🎉", post_id=post1.id, author_id=bob.id),
    Comment(text="Amazing photo!", post_id=post2.id, author_id=alice.id),
    Comment(text="Congrats! 🎊", post_id=post3.id, author_id=alice.id),
    Comment(text="Well done!", post_id=post3.id, author_id=bob.id),
])
db.commit()

print("Seeded: 3 users, 3 posts, 3 likes, 4 comments")
print("Login with username: alice/bob/charlie, password: password")
db.close()
