import SwiftUI
import PhotosUI

@MainActor
class CreatePostViewModel: ObservableObject {
    @Published var text = ""
    @Published var selectedImage: UIImage?
    @Published var selectedItem: PhotosPickerItem?
    @Published var isPosting = false
    @Published var didPost = false
    
    private let api = APIService.shared
    
    func loadImage() async {
        guard let item = selectedItem else { return }
        if let data = try? await item.loadTransferable(type: Data.self),
           let image = UIImage(data: data) {
            self.selectedImage = image
        }
    }
    
    func createPost() {
        guard !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        isPosting = true
        Task {
            do {
                _ = try await api.createPost(text: text.trimmingCharacters(in: .whitespacesAndNewlines))
                text = ""
                selectedImage = nil
                selectedItem = nil
                isPosting = false
                didPost = true
            } catch {
                print("Error creating post: \(error)")
                isPosting = false
            }
        }
    }
    
    func reset() {
        text = ""
        selectedImage = nil
        selectedItem = nil
        didPost = false
    }
}
