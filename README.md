# Social Feed App

A social feed app (Instagram-lite) built as a monorepo with Android + iOS clients and three interchangeable REST API backends.

## Architecture

```
┌─────────────┐     ┌─────────────┐
│   Android    │     │     iOS     │
│  Kotlin +    │     │  Swift +    │
│  Compose     │     │  SwiftUI    │
└──────┬───────┘     └──────┬──────┘
       │    REST API / JWT   │
       └────────┬────────────┘
                │
    ┌───────────┼───────────┐
    │           │           │
┌───▼───┐  ┌───▼───┐  ┌────▼──┐
│  JS   │  │  Py   │  │  C#   │
│Express│  │FastAPI│  │ASP.NET│
└───┬───┘  └───┬───┘  └───┬───┘
    └───────┬───┘──────────┘
            │
       ┌────▼────┐
       │ SQLite  │
       └─────────┘
```

All three backends serve **identical APIs** on **port 3000**. Run one at a time — the mobile apps work with any of them.

## Backends

| | Stack | Directory | Run |
|---|---|---|---|
| 📦 **JS** | Node.js + Express + TypeScript + Prisma | `backend-js/` | `npm run dev` |
| 🐍 **Python** | FastAPI + SQLAlchemy + Pydantic | `backend-py/` | `uvicorn main:app --port 3000` |
| 🔷 **C#** | ASP.NET Core + EF Core | `backend-cs/` | `dotnet run --urls http://localhost:3000` |

### API Endpoints

All backends implement these endpoints identically:

**Auth**
- `POST /api/auth/register` — Register (username, password) → JWT
- `POST /api/auth/login` — Login → JWT
- `GET /api/auth/me` — Current user (🔒)

**Posts**
- `GET /api/posts?limit=20&offset=0` — List posts (paginated)
- `POST /api/posts` — Create post (🔒)
- `GET /api/posts/:id` — Get post with comments
- `DELETE /api/posts/:id` — Delete own post (🔒)

**Likes**
- `POST /api/posts/:id/like` — Toggle like (🔒)
- `GET /api/posts/:id/likes` — Like count + liked status

**Comments**
- `GET /api/posts/:id/comments` — List comments
- `POST /api/posts/:id/comments` — Add comment (🔒)
- `DELETE /api/comments/:id` — Delete own comment (🔒)

**Users**
- `GET /api/users/:id` — Get profile
- `PUT /api/users/me` — Update profile (🔒)

🔒 = Requires `Authorization: Bearer <token>` header

### Seed Data

All backends seed the same test data:
- **Users:** `alice`, `bob`, `charlie` (password: `password`)
- **Posts:** 3 sample posts with likes and comments

## Mobile Apps

### Android (`android/`)
- Kotlin + Jetpack Compose + Material 3
- Retrofit + OkHttp for API calls
- JWT stored in SharedPreferences
- MVVM architecture

### iOS (`ios/`)
- Swift + SwiftUI
- URLSession for API calls
- JWT stored in UserDefaults
- MVVM architecture

## Features
- 🔐 Login / Register with JWT auth
- 📰 Home feed with paginated posts
- ✍️ Create posts with text and optional images
- ❤️ Like and comment on posts
- 👤 User profiles
- ⚙️ Settings (dark mode, logout)

## Screens
1. **Login / Register** — Auth screen on first launch
2. **Home Feed** — Scrolling post feed from API
3. **Create Post** — Text + image composer
4. **Post Detail** — Full post with comments
5. **Profile** — User info and their posts
6. **Settings** — Account and app settings

## Quick Start

### 1. Start a backend (pick one)

**Node.js:**
```bash
cd backend-js
npm install
cp .env.example .env
npx prisma db push
npx tsx src/seed.ts
npm run dev
```

**Python:**
```bash
cd backend-py
pip install -r requirements.txt
python seed.py
uvicorn main:app --port 3000
```

**C#:**
```bash
cd backend-cs
dotnet run --urls http://localhost:3000
# Seeds automatically on first run
```

### 2. Build & run mobile apps

**Android:**
```bash
cd android
./gradlew assembleDebug
# Install on emulator (uses 10.0.2.2:3000 → host localhost)
adb install app/build/outputs/apk/debug/app-debug.apk
```

**iOS:**
```bash
cd ios
xcodebuild -project SocialFeed.xcodeproj -scheme SocialFeed \
  -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 15' build
# Uses localhost:3000 directly from simulator
```

## Repository

- **GitHub:** [acticom-agent/social-feed-app](https://github.com/acticom-agent/social-feed-app)
