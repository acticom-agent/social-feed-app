import Foundation

final class APIService {
    static let shared = APIService()
    
    private let baseURL = "http://localhost:3000"
    private let session = URLSession.shared
    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        return d
    }()
    
    var token: String? {
        get { UserDefaults.standard.string(forKey: "authToken") }
        set { UserDefaults.standard.set(newValue, forKey: "authToken") }
    }
    
    // MARK: - Generic Request
    
    private func request<T: Decodable>(_ method: String, path: String, body: (any Encodable)? = nil, auth: Bool = false) async throws -> T {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        var req = URLRequest(url: url)
        req.httpMethod = method
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if auth, let token = token {
            req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        if let body = body {
            req.httpBody = try JSONEncoder().encode(body)
        }
        let (data, response) = try await session.data(for: req)
        guard let http = response as? HTTPURLResponse else { throw APIError.invalidResponse }
        guard (200...299).contains(http.statusCode) else {
            throw APIError.httpError(http.statusCode, String(data: data, encoding: .utf8) ?? "")
        }
        return try decoder.decode(T.self, from: data)
    }
    
    private func requestVoid(_ method: String, path: String, body: (any Encodable)? = nil, auth: Bool = false) async throws {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        var req = URLRequest(url: url)
        req.httpMethod = method
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if auth, let token = token {
            req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        if let body = body {
            req.httpBody = try JSONEncoder().encode(body)
        }
        let (_, response) = try await session.data(for: req)
        guard let http = response as? HTTPURLResponse, (200...299).contains(http.statusCode) else {
            throw APIError.invalidResponse
        }
    }
    
    // MARK: - Auth
    
    func login(username: String, password: String) async throws -> AuthResponse {
        let resp: AuthResponse = try await request("POST", path: "/api/auth/login", body: LoginRequest(username: username, password: password))
        token = resp.token
        return resp
    }
    
    func register(username: String, password: String) async throws -> AuthResponse {
        let resp: AuthResponse = try await request("POST", path: "/api/auth/register", body: LoginRequest(username: username, password: password))
        token = resp.token
        return resp
    }
    
    func me() async throws -> APIUser {
        let resp: MeResponse = try await request("GET", path: "/api/auth/me", auth: true)
        return resp.user
    }
    
    // MARK: - Posts
    
    func fetchPosts(limit: Int = 20, offset: Int = 0) async throws -> [APIPost] {
        let resp: PostsResponse = try await request("GET", path: "/api/posts?limit=\(limit)&offset=\(offset)")
        return resp.posts
    }
    
    func createPost(text: String, imageUrl: String? = nil) async throws -> APIPost {
        let resp: PostResponse = try await request("POST", path: "/api/posts", body: CreatePostRequest(text: text, imageUrl: imageUrl), auth: true)
        return resp.post
    }
    
    func deletePost(id: Int) async throws {
        try await requestVoid("DELETE", path: "/api/posts/\(id)", auth: true)
    }
    
    // MARK: - Likes
    
    func toggleLike(postId: Int) async throws -> LikeResponse {
        return try await request("POST", path: "/api/posts/\(postId)/like", auth: true)
    }
    
    // MARK: - Comments
    
    func fetchComments(postId: Int) async throws -> [APIComment] {
        let resp: CommentsResponse = try await request("GET", path: "/api/posts/\(postId)/comments")
        return resp.comments
    }
    
    func addComment(postId: Int, text: String) async throws -> APIComment {
        let resp: CommentResponse = try await request("POST", path: "/api/posts/\(postId)/comments", body: CreateCommentRequest(text: text), auth: true)
        return resp.comment
    }
    
    // MARK: - Users
    
    func fetchUser(id: Int) async throws -> APIUser {
        let resp: UserResponse = try await request("GET", path: "/api/users/\(id)")
        return resp.user
    }
    
    func updateMe(username: String?, avatarUrl: String?) async throws -> APIUser {
        let resp: UserResponse = try await request("PUT", path: "/api/users/me", body: UpdateUserRequest(username: username, avatarUrl: avatarUrl), auth: true)
        return resp.user
    }
    
    func logout() {
        token = nil
    }
}

enum APIError: LocalizedError {
    case invalidURL
    case invalidResponse
    case httpError(Int, String)
    
    var errorDescription: String? {
        switch self {
        case .invalidURL: return "Invalid URL"
        case .invalidResponse: return "Invalid response"
        case .httpError(let code, let msg): return "HTTP \(code): \(msg)"
        }
    }
}
