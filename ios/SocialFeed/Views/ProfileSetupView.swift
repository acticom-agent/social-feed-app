import SwiftUI
import PhotosUI

struct ProfileSetupView: View {
    @StateObject private var viewModel = ProfileViewModel()
    @AppStorage("hasCompletedSetup") private var hasCompletedSetup = false
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 32) {
                Spacer()
                
                Text("Welcome to SocialFeed")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text("Set up your profile to get started")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                
                // Avatar Picker
                PhotosPicker(selection: $viewModel.selectedItem, matching: .images) {
                    if let image = viewModel.avatarImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 120, height: 120)
                            .clipShape(Circle())
                            .overlay(Circle().stroke(.primary.opacity(0.2), lineWidth: 2))
                    } else {
                        ZStack {
                            Circle()
                                .fill(.ultraThinMaterial)
                                .frame(width: 120, height: 120)
                            Image(systemName: "camera.fill")
                                .font(.title)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .onChange(of: viewModel.selectedItem) {
                    Task { await viewModel.loadSelectedImage() }
                }
                
                TextField("Username", text: $viewModel.username)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal, 40)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
                
                Button {
                    viewModel.createUser()
                    hasCompletedSetup = true
                } label: {
                    Text("Get Started")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(.primary)
                        .foregroundStyle(Color(.systemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding(.horizontal, 40)
                .disabled(viewModel.username.trimmingCharacters(in: .whitespaces).isEmpty)
                
                Spacer()
                Spacer()
            }
        }
    }
}
