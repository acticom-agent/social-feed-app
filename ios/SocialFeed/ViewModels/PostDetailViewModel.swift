import SwiftUI

@MainActor
class PostDetailViewModel: ObservableObject {
    @Published var comments: [APIComment] = []
    @Published var newCommentText = ""
    @Published var liked = false
    @Published var currentLikeCount = 0
    
    let post: APIPost
    private let api = APIService.shared
    
    init(post: APIPost) {
        self.post = post
        self.currentLikeCount = post._count?.likes ?? 0
        loadComments()
    }
    
    func loadComments() {
        Task {
            do {
                comments = try await api.fetchComments(postId: post.id)
            } catch {
                print("Error loading comments: \(error)")
            }
        }
    }
    
    func addComment() {
        let trimmed = newCommentText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        Task {
            do {
                let comment = try await api.addComment(postId: post.id, text: trimmed)
                comments.append(comment)
                newCommentText = ""
            } catch {
                print("Error adding comment: \(error)")
            }
        }
    }
    
    func toggleLike() {
        Task {
            do {
                let resp = try await api.toggleLike(postId: post.id)
                liked = resp.liked
                currentLikeCount = resp.likesCount
            } catch {
                print("Error toggling like: \(error)")
            }
        }
    }
}
