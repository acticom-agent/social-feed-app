# Social Feed App (Local-Only)

A local-first social feed app (Instagram-lite) built as a monorepo with Android and iOS clients. No backend required — all data lives on the device.

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

## Build

### Android
```bash
cd android && ./gradlew assembleDebug
```

### iOS
```bash
cd ios && xcodebuild -scheme SocialFeed -destination 'platform=iOS Simulator,name=iPhone 15'
```
