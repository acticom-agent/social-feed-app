import CoreData
import Combine

class CommentRepository: ObservableObject {
    private let context: NSManagedObjectContext
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.context = context
    }
    
    func fetchComments(for postId: UUID) -> [CommentEntity] {
        let request = NSFetchRequest<CommentEntity>(entityName: "CommentEntity")
        request.predicate = NSPredicate(format: "postId == %@", postId as CVarArg)
        request.sortDescriptors = [NSSortDescriptor(key: "createdAt", ascending: true)]
        return (try? context.fetch(request)) ?? []
    }
    
    func commentCount(for postId: UUID) -> Int {
        let request = NSFetchRequest<CommentEntity>(entityName: "CommentEntity")
        request.predicate = NSPredicate(format: "postId == %@", postId as CVarArg)
        return (try? context.count(for: request)) ?? 0
    }
    
    @discardableResult
    func addComment(postId: UUID, authorId: UUID, text: String) -> CommentEntity {
        let comment = CommentEntity(context: context)
        comment.id = UUID()
        comment.postId = postId
        comment.authorId = authorId
        comment.text = text
        comment.createdAt = Date()
        try? context.save()
        return comment
    }
    
    func exportJSON() -> String {
        let request = NSFetchRequest<CommentEntity>(entityName: "CommentEntity")
        let comments = (try? context.fetch(request)) ?? []
        let data = comments.map { [
            "id": $0.id?.uuidString ?? "",
            "postId": $0.postId?.uuidString ?? "",
            "authorId": $0.authorId?.uuidString ?? "",
            "text": $0.text ?? "",
            "createdAt": ISO8601DateFormatter().string(from: $0.createdAt ?? Date())
        ]}
        guard let json = try? JSONSerialization.data(withJSONObject: data, options: .prettyPrinted) else { return "[]" }
        return String(data: json, encoding: .utf8) ?? "[]"
    }
}
