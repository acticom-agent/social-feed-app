import SwiftUI
import PhotosUI

@MainActor
class ProfileViewModel: ObservableObject {
    @Published var user: APIUser?
    @Published var userPosts: [APIPost] = []
    @Published var username = ""
    @Published var selectedItem: PhotosPickerItem?
    @Published var avatarImage: UIImage?
    
    private let api = APIService.shared
    
    func loadProfile() {
        Task {
            do {
                let me = try await api.me()
                user = me
                username = me.username
                // Load user's posts from the feed
                let allPosts = try await api.fetchPosts(limit: 100)
                userPosts = allPosts.filter { $0.author.id == me.id }
            } catch {
                print("Error loading profile: \(error)")
            }
        }
    }
    
    func loadSelectedImage() async {
        guard let item = selectedItem else { return }
        if let data = try? await item.loadTransferable(type: Data.self),
           let image = UIImage(data: data) {
            self.avatarImage = image
        }
    }
    
    func updateProfile() {
        Task {
            do {
                let updated = try await api.updateMe(username: username, avatarUrl: nil)
                user = updated
            } catch {
                print("Error updating profile: \(error)")
            }
        }
    }
    
    var postCount: Int { userPosts.count }
}
