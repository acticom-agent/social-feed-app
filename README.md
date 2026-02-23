# Social Feed App

A social feed app (Instagram-lite) built as a monorepo with Android and iOS clients and a REST API backend.

## Architecture
- **Pattern:** MVVM + Repository
- **Storage:** Room (Android) / Core Data (iOS)
- **Data Flow:** View → ViewModel → Repository → Local Database

## Platforms

### Android (`android/`)
- Kotlin + Jetpack Compose
- Room + Coroutines + Flow
- Material 3 Design
- DataStore for settings

### iOS (`ios/`)
- Swift + SwiftUI
- Core Data + Combine
- FileManager for media

## Features
- Local user profile (username + avatar)
- Create posts with text and optional images
- Like and comment on posts
- Home feed with infinite scroll
- Profile view with user's posts
- Settings (clear data, export JSON, reset profile)
- Dark mode support
- Full offline functionality

## Screens
1. **Profile Setup** - First launch username/avatar setup
2. **Home Feed** - Vertical scrolling post feed
3. **Create Post** - Text + image composer
4. **Post Detail** - Full post with comments
5. **Profile** - User info and their posts
6. **Settings** - Data management

## Data Model
- **User** - id, username, avatarPath
- **Post** - id, authorId, text, imagePath, createdAt
- **Like** - postId, userId
- **Comment** - id, postId, authorId, text, createdAt

### Backend (`backend/`)
- Node.js + TypeScript + Express
- Prisma ORM + SQLite
- JWT authentication
- REST API: auth, users, posts, likes, comments
- File upload for images (Multer)

See [`backend/README.md`](backend/README.md) for full API docs.

## Build

### Backend
```bash
cd backend && npm install && cp .env.example .env && npx prisma db push && npm run dev
```

### Android
```bash
cd android && ./gradlew assembleDebug
```

### iOS
```bash
cd ios && xcodebuild -scheme SocialFeed -destination 'platform=iOS Simulator,name=iPhone 15'
```
