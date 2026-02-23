import CoreData
import Combine

class LikeRepository: ObservableObject {
    private let context: NSManagedObjectContext
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.context = context
    }
    
    func likeCount(for postId: UUID) -> Int {
        let request = NSFetchRequest<LikeEntity>(entityName: "LikeEntity")
        request.predicate = NSPredicate(format: "postId == %@", postId as CVarArg)
        return (try? context.count(for: request)) ?? 0
    }
    
    func isLiked(postId: UUID, userId: UUID) -> Bool {
        let request = NSFetchRequest<LikeEntity>(entityName: "LikeEntity")
        request.predicate = NSPredicate(format: "postId == %@ AND userId == %@", postId as CVarArg, userId as CVarArg)
        return ((try? context.count(for: request)) ?? 0) > 0
    }
    
    func toggleLike(postId: UUID, userId: UUID) {
        let request = NSFetchRequest<LikeEntity>(entityName: "LikeEntity")
        request.predicate = NSPredicate(format: "postId == %@ AND userId == %@", postId as CVarArg, userId as CVarArg)
        
        if let existing = try? context.fetch(request).first {
            context.delete(existing)
        } else {
            let like = LikeEntity(context: context)
            like.postId = postId
            like.userId = userId
        }
        try? context.save()
    }
    
    func exportJSON() -> String {
        let request = NSFetchRequest<LikeEntity>(entityName: "LikeEntity")
        let likes = (try? context.fetch(request)) ?? []
        let data = likes.map { [
            "postId": $0.postId?.uuidString ?? "",
            "userId": $0.userId?.uuidString ?? ""
        ]}
        guard let json = try? JSONSerialization.data(withJSONObject: data, options: .prettyPrinted) else { return "[]" }
        return String(data: json, encoding: .utf8) ?? "[]"
    }
}
