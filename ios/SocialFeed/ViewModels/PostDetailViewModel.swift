import SwiftUI
import CoreData

class PostDetailViewModel: ObservableObject {
    @Published var comments: [CommentEntity] = []
    @Published var newCommentText = ""
    
    let post: PostEntity
    private let commentRepo: CommentRepository
    private let likeRepo: LikeRepository
    private let userRepo: UserRepository
    
    init(post: PostEntity, context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.post = post
        self.commentRepo = CommentRepository(context: context)
        self.likeRepo = LikeRepository(context: context)
        self.userRepo = UserRepository(context: context)
        loadComments()
    }
    
    func loadComments() {
        guard let postId = post.id else { return }
        comments = commentRepo.fetchComments(for: postId)
    }
    
    func addComment() {
        let trimmed = newCommentText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty,
              let postId = post.id,
              let userId = userRepo.currentUser?.id else { return }
        commentRepo.addComment(postId: postId, authorId: userId, text: trimmed)
        newCommentText = ""
        loadComments()
    }
    
    func likeCount() -> Int {
        guard let postId = post.id else { return 0 }
        return likeRepo.likeCount(for: postId)
    }
    
    func isLiked() -> Bool {
        guard let postId = post.id, let userId = userRepo.currentUser?.id else { return false }
        return likeRepo.isLiked(postId: postId, userId: userId)
    }
    
    func toggleLike() {
        guard let postId = post.id, let userId = userRepo.currentUser?.id else { return }
        likeRepo.toggleLike(postId: postId, userId: userId)
        objectWillChange.send()
    }
    
    func getAuthor(for authorId: UUID?) -> UserEntity? {
        guard let id = authorId else { return nil }
        return userRepo.getUser(by: id)
    }
}
