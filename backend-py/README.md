# Social Feed - Python Backend

FastAPI backend, functionally identical to the Node.js version.

## Setup

```bash
cd backend-py
pip install -r requirements.txt
python seed.py
python -m uvicorn main:app --port 3000
```

## Seed Users

- alice / password
- bob / password  
- charlie / password

## API Endpoints

- `POST /api/auth/register` - Register
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Current user (auth required)
- `GET /api/posts` - List posts (?limit=&offset=)
- `GET /api/posts/:id` - Get post with comments
- `POST /api/posts` - Create post (auth, multipart: text + image)
- `DELETE /api/posts/:id` - Delete post (auth, owner only)
- `POST /api/posts/:id/like` - Toggle like (auth)
- `GET /api/posts/:id/likes` - Like count + status
- `GET /api/posts/:id/comments` - List comments (?limit=&offset=)
- `POST /api/posts/:id/comments` - Add comment (auth)
- `DELETE /api/comments/:id` - Delete comment (auth, owner only)
- `GET /api/users/:id` - Get user profile
- `PUT /api/users/me` - Update profile (auth)
- `GET /api/health` - Health check
