import CoreData
import Combine

class PostRepository: ObservableObject {
    private let context: NSManagedObjectContext
    @Published var posts: [PostEntity] = []
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.context = context
    }
    
    func fetchAllPosts() {
        let request = NSFetchRequest<PostEntity>(entityName: "PostEntity")
        request.sortDescriptors = [NSSortDescriptor(key: "createdAt", ascending: false)]
        posts = (try? context.fetch(request)) ?? []
    }
    
    func fetchPosts(by authorId: UUID) -> [PostEntity] {
        let request = NSFetchRequest<PostEntity>(entityName: "PostEntity")
        request.predicate = NSPredicate(format: "authorId == %@", authorId as CVarArg)
        request.sortDescriptors = [NSSortDescriptor(key: "createdAt", ascending: false)]
        return (try? context.fetch(request)) ?? []
    }
    
    @discardableResult
    func createPost(authorId: UUID, text: String, imagePath: String?) -> PostEntity {
        let post = PostEntity(context: context)
        post.id = UUID()
        post.authorId = authorId
        post.text = text
        post.imagePath = imagePath
        post.createdAt = Date()
        try? context.save()
        fetchAllPosts()
        return post
    }
    
    func deletePost(_ post: PostEntity) {
        if let imagePath = post.imagePath {
            ImageManager.shared.deleteImage(named: imagePath)
        }
        context.delete(post)
        try? context.save()
        fetchAllPosts()
    }
    
    func exportJSON() -> String {
        let request = NSFetchRequest<PostEntity>(entityName: "PostEntity")
        let posts = (try? context.fetch(request)) ?? []
        let data = posts.map { [
            "id": $0.id?.uuidString ?? "",
            "authorId": $0.authorId?.uuidString ?? "",
            "text": $0.text ?? "",
            "imagePath": $0.imagePath ?? "",
            "createdAt": ISO8601DateFormatter().string(from: $0.createdAt ?? Date())
        ]}
        guard let json = try? JSONSerialization.data(withJSONObject: data, options: .prettyPrinted) else { return "[]" }
        return String(data: json, encoding: .utf8) ?? "[]"
    }
}
