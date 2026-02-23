import CoreData
import Combine

class UserRepository: ObservableObject {
    private let context: NSManagedObjectContext
    @Published var currentUser: UserEntity?
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.context = context
        fetchCurrentUser()
    }
    
    func fetchCurrentUser() {
        let request = NSFetchRequest<UserEntity>(entityName: "UserEntity")
        request.fetchLimit = 1
        currentUser = try? context.fetch(request).first
    }
    
    @discardableResult
    func createUser(username: String, avatarPath: String?) -> UserEntity {
        let user = UserEntity(context: context)
        user.id = UUID()
        user.username = username
        user.avatarPath = avatarPath
        try? context.save()
        currentUser = user
        return user
    }
    
    func getUser(by id: UUID) -> UserEntity? {
        let request = NSFetchRequest<UserEntity>(entityName: "UserEntity")
        request.predicate = NSPredicate(format: "id == %@", id as CVarArg)
        request.fetchLimit = 1
        return try? context.fetch(request).first
    }
    
    func updateUser(username: String, avatarPath: String?) {
        guard let user = currentUser else { return }
        user.username = username
        user.avatarPath = avatarPath
        try? context.save()
    }
    
    func exportJSON() -> String {
        let request = NSFetchRequest<UserEntity>(entityName: "UserEntity")
        let users = (try? context.fetch(request)) ?? []
        let data = users.map { [
            "id": $0.id?.uuidString ?? "",
            "username": $0.username ?? "",
            "avatarPath": $0.avatarPath ?? ""
        ]}
        guard let json = try? JSONSerialization.data(withJSONObject: data, options: .prettyPrinted) else { return "[]" }
        return String(data: json, encoding: .utf8) ?? "[]"
    }
}
