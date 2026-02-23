# Social Feed Backend

REST API backend for the Social Feed App, built with Node.js, Express, TypeScript, Prisma, and SQLite.

## Quick Start

```bash
cd backend
npm install
cp .env.example .env
npx prisma db push
npm run seed    # optional: populate sample data
npm run dev     # starts dev server on http://localhost:3000
```

## Sample Accounts

After seeding: `alice`, `bob`, `charlie` — all with password `password`.

## API Endpoints

### Auth
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Register (body: `username`, `password`) → JWT |
| POST | `/api/auth/login` | No | Login → JWT |
| GET | `/api/auth/me` | Yes | Get current user |

### Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/users/:id` | No | Get user profile |
| PUT | `/api/users/me` | Yes | Update profile (`username`, `avatarUrl`) |

### Posts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/posts` | No | List posts (query: `limit`, `offset`) |
| POST | `/api/posts` | Yes | Create post (multipart: `text`, `image`) |
| GET | `/api/posts/:id` | No | Get post with comments |
| DELETE | `/api/posts/:id` | Yes | Delete own post |

### Likes
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/posts/:id/like` | Yes | Toggle like |
| GET | `/api/posts/:id/likes` | No | Like count + current user liked |

### Comments
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/posts/:id/comments` | No | List comments (`limit`, `offset`) |
| POST | `/api/posts/:id/comments` | Yes | Add comment (`text`) |
| DELETE | `/api/comments/:id` | Yes | Delete own comment |

### Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |

## Authentication

Include JWT token in the `Authorization` header:
```
Authorization: Bearer <token>
```

## Tech Stack
- **Runtime:** Node.js + TypeScript
- **Framework:** Express
- **ORM:** Prisma
- **Database:** SQLite
- **Auth:** JWT (jsonwebtoken + bcryptjs)
- **Upload:** Multer (images stored in `uploads/`)
