import SwiftUI
import Combine

@MainActor
class FeedViewModel: ObservableObject {
    @Published var posts: [APIPost] = []
    @Published var likedPostIds: Set<Int> = []
    @Published var likeCounts: [Int: Int] = [:]
    
    private let api = APIService.shared
    
    func loadPosts() {
        Task {
            do {
                posts = try await api.fetchPosts()
                for post in posts {
                    likeCounts[post.id] = post._count?.likes ?? 0
                }
            } catch {
                print("Error loading posts: \(error)")
            }
        }
    }
    
    func likeCount(for post: APIPost) -> Int {
        likeCounts[post.id] ?? post._count?.likes ?? 0
    }
    
    func commentCount(for post: APIPost) -> Int {
        post._count?.comments ?? 0
    }
    
    func isLiked(_ post: APIPost) -> Bool {
        likedPostIds.contains(post.id)
    }
    
    func toggleLike(_ post: APIPost) {
        Task {
            do {
                let resp = try await api.toggleLike(postId: post.id)
                if resp.liked {
                    likedPostIds.insert(post.id)
                } else {
                    likedPostIds.remove(post.id)
                }
                likeCounts[post.id] = resp.likesCount
            } catch {
                print("Error toggling like: \(error)")
            }
        }
    }
}
