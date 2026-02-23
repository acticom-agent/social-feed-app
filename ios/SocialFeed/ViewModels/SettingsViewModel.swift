import SwiftUI
import CoreData

class SettingsViewModel: ObservableObject {
    @Published var showExportSheet = false
    @Published var exportedJSON = ""
    @Published var showClearConfirmation = false
    @Published var showResetConfirmation = false
    
    private let userRepo: UserRepository
    private let postRepo: PostRepository
    private let likeRepo: LikeRepository
    private let commentRepo: CommentRepository
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.userRepo = UserRepository(context: context)
        self.postRepo = PostRepository(context: context)
        self.likeRepo = LikeRepository(context: context)
        self.commentRepo = CommentRepository(context: context)
    }
    
    func exportAsJSON() -> String {
        let json: [String: Any] = [
            "users": userRepo.exportJSON(),
            "posts": postRepo.exportJSON(),
            "likes": likeRepo.exportJSON(),
            "comments": commentRepo.exportJSON()
        ]
        guard let data = try? JSONSerialization.data(withJSONObject: json, options: .prettyPrinted) else { return "{}" }
        return String(data: data, encoding: .utf8) ?? "{}"
    }
    
    func clearAllData() {
        PersistenceController.shared.clearAllData()
        ImageManager.shared.deleteAllImages()
    }
    
    func resetProfile() {
        clearAllData()
        UserDefaults.standard.set(false, forKey: "hasCompletedSetup")
    }
}
