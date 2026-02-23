import SwiftUI
import PhotosUI
import CoreData

class ProfileViewModel: ObservableObject {
    @Published var user: UserEntity?
    @Published var userPosts: [PostEntity] = []
    @Published var username = ""
    @Published var selectedItem: PhotosPickerItem?
    @Published var avatarImage: UIImage?
    
    private let userRepo: UserRepository
    private let postRepo: PostRepository
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.userRepo = UserRepository(context: context)
        self.postRepo = PostRepository(context: context)
        loadProfile()
    }
    
    func loadProfile() {
        userRepo.fetchCurrentUser()
        user = userRepo.currentUser
        if let user = user {
            username = user.username ?? ""
            if let path = user.avatarPath {
                avatarImage = ImageManager.shared.loadImage(named: path)
            }
            if let userId = user.id {
                userPosts = postRepo.fetchPosts(by: userId)
            }
        }
    }
    
    func loadSelectedImage() async {
        guard let item = selectedItem else { return }
        if let data = try? await item.loadTransferable(type: Data.self),
           let image = UIImage(data: data) {
            await MainActor.run {
                self.avatarImage = image
            }
        }
    }
    
    func createUser() {
        var avatarPath: String?
        if let image = avatarImage {
            avatarPath = ImageManager.shared.saveImage(image, name: "avatar.jpg")
        }
        userRepo.createUser(username: username, avatarPath: avatarPath)
        user = userRepo.currentUser
    }
    
    func updateProfile() {
        var avatarPath: String?
        if let image = avatarImage {
            avatarPath = ImageManager.shared.saveImage(image, name: "avatar.jpg")
        }
        userRepo.updateUser(username: username, avatarPath: avatarPath)
        loadProfile()
    }
    
    var postCount: Int { userPosts.count }
}
