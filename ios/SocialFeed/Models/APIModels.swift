import Foundation

// MARK: - Auth
struct AuthResponse: Codable {
    let token: String
    let user: APIUser
}

struct LoginRequest: Codable {
    let username: String
    let password: String
}

// MARK: - User
struct APIUser: Codable, Identifiable {
    let id: Int
    let username: String
    let avatarUrl: String?
    let _count: UserCount?
    
    struct UserCount: Codable {
        let posts: Int?
    }
}

// MARK: - Post
struct APIPost: Codable, Identifiable {
    let id: Int
    let text: String
    let imageUrl: String?
    let createdAt: String
    let author: APIUser
    let _count: PostCount?
    
    struct PostCount: Codable {
        let likes: Int?
        let comments: Int?
    }
    
    var createdAtDate: Date {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f.date(from: createdAt) ?? Date()
    }
}

struct PostsResponse: Codable {
    let posts: [APIPost]
}

struct CreatePostRequest: Codable {
    let text: String
    let imageUrl: String?
}

// MARK: - Like
struct LikeResponse: Codable {
    let liked: Bool
    let likesCount: Int
}

// MARK: - Comment
struct APIComment: Codable, Identifiable {
    let id: Int
    let text: String
    let createdAt: String
    let author: APIUser
    
    var createdAtDate: Date {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f.date(from: createdAt) ?? Date()
    }
}

struct CommentsResponse: Codable {
    let comments: [APIComment]
}

struct CreateCommentRequest: Codable {
    let text: String
}

struct UpdateUserRequest: Codable {
    let username: String?
    let avatarUrl: String?
}

struct MeResponse: Codable {
    let user: APIUser
}

struct UserResponse: Codable {
    let user: APIUser
}

struct PostResponse: Codable {
    let post: APIPost
}

struct CommentResponse: Codable {
    let comment: APIComment
}
