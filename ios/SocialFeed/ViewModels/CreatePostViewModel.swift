import SwiftUI
import PhotosUI
import CoreData

class CreatePostViewModel: ObservableObject {
    @Published var text = ""
    @Published var selectedImage: UIImage?
    @Published var selectedItem: PhotosPickerItem?
    @Published var isPosting = false
    @Published var didPost = false
    
    private let postRepo: PostRepository
    private let userRepo: UserRepository
    
    init(context: NSManagedObjectContext = PersistenceController.shared.container.viewContext) {
        self.postRepo = PostRepository(context: context)
        self.userRepo = UserRepository(context: context)
    }
    
    func loadImage() async {
        guard let item = selectedItem else { return }
        if let data = try? await item.loadTransferable(type: Data.self),
           let image = UIImage(data: data) {
            await MainActor.run {
                self.selectedImage = image
            }
        }
    }
    
    func createPost() {
        guard !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || selectedImage != nil else { return }
        guard let userId = userRepo.currentUser?.id else { return }
        
        isPosting = true
        
        var imagePath: String?
        if let image = selectedImage {
            imagePath = ImageManager.shared.saveImage(image)
        }
        
        postRepo.createPost(authorId: userId, text: text.trimmingCharacters(in: .whitespacesAndNewlines), imagePath: imagePath)
        
        text = ""
        selectedImage = nil
        selectedItem = nil
        isPosting = false
        didPost = true
    }
    
    func reset() {
        text = ""
        selectedImage = nil
        selectedItem = nil
        didPost = false
    }
}
