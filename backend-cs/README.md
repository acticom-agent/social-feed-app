# Social Feed API — C# / ASP.NET Core

A C# port of the Node.js social feed backend, functionally identical API.

## Tech Stack
- ASP.NET Core 8
- Entity Framework Core + SQLite
- JWT Authentication
- BCrypt password hashing

## Quick Start

```bash
dotnet run --urls http://localhost:3000
```

## Default Users
- alice / password
- bob / password  
- charlie / password

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /api/auth/register | No | Register |
| POST | /api/auth/login | No | Login |
| GET | /api/auth/me | Yes | Current user |
| GET | /api/posts | No | List posts |
| GET | /api/posts/:id | No | Get post |
| POST | /api/posts | Yes | Create post (multipart) |
| DELETE | /api/posts/:id | Yes | Delete post |
| POST | /api/posts/:id/like | Yes | Toggle like |
| GET | /api/posts/:id/likes | No | Like count |
| GET | /api/posts/:id/comments | No | List comments |
| POST | /api/posts/:id/comments | Yes | Add comment |
| DELETE | /api/comments/:id | Yes | Delete comment |
| GET | /api/users/:id | No | Get user |
| PUT | /api/users/me | Yes | Update profile |
| GET | /api/health | No | Health check |
