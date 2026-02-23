import SwiftUI
import Combine
import CoreData

class FeedViewModel: ObservableObject {
    @Published var posts: [PostEntity] = []
    
    private let postRepo: PostRepository
    private let userRepo: UserRepository
    private let likeRepo: LikeRepository
    private let commentRepo: CommentRepository
    private var cancellables = Set<AnyCancellable>()
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.postRepo = PostRepository(context: context)
        self.userRepo = UserRepository(context: context)
        self.likeRepo = LikeRepository(context: context)
        self.commentRepo = CommentRepository(context: context)
        
        postRepo.$posts
            .assign(to: &$posts)
    }
    
    func loadPosts() {
        postRepo.fetchAllPosts()
    }
    
    func getAuthor(for post: PostEntity) -> UserEntity? {
        guard let authorId = post.authorId else { return nil }
        return userRepo.getUser(by: authorId)
    }
    
    func likeCount(for post: PostEntity) -> Int {
        guard let postId = post.id else { return 0 }
        return likeRepo.likeCount(for: postId)
    }
    
    func commentCount(for post: PostEntity) -> Int {
        guard let postId = post.id else { return 0 }
        return commentRepo.commentCount(for: postId)
    }
    
    func isLiked(_ post: PostEntity) -> Bool {
        guard let postId = post.id, let userId = userRepo.currentUser?.id else { return false }
        return likeRepo.isLiked(postId: postId, userId: userId)
    }
    
    func toggleLike(_ post: PostEntity) {
        guard let postId = post.id, let userId = userRepo.currentUser?.id else { return }
        likeRepo.toggleLike(postId: postId, userId: userId)
        objectWillChange.send()
    }
}
