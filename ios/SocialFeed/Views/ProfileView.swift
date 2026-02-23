import SwiftUI

struct ProfileView: View {
    @StateObject private var viewModel = ProfileViewModel()
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                VStack(spacing: 12) {
                    AvatarView(avatarUrl: viewModel.user?.avatarUrl, size: 80)
                    
                    Text(viewModel.user?.username ?? "Unknown")
                        .font(.title2)
                        .fontWeight(.bold)
                    
                    HStack(spacing: 40) {
                        VStack {
                            Text("\(viewModel.postCount)")
                                .font(.headline)
                            Text("Posts")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .padding(.top, 20)
                
                Divider()
                
                if viewModel.userPosts.isEmpty {
                    VStack(spacing: 8) {
                        Image(systemName: "camera")
                            .font(.system(size: 40))
                            .foregroundStyle(.secondary)
                        Text("No Posts Yet")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.top, 40)
                } else {
                    LazyVGrid(columns: [
                        GridItem(.flexible(), spacing: 2),
                        GridItem(.flexible(), spacing: 2),
                        GridItem(.flexible(), spacing: 2)
                    ], spacing: 2) {
                        ForEach(viewModel.userPosts) { post in
                            NavigationLink {
                                PostDetailView(post: post)
                            } label: {
                                ZStack {
                                    Rectangle()
                                        .fill(Color(.secondarySystemBackground))
                                        .aspectRatio(1, contentMode: .fill)
                                    Text(post.text)
                                        .font(.caption2)
                                        .padding(4)
                                        .lineLimit(3)
                                }
                            }
                        }
                    }
                }
            }
        }
        .navigationTitle("Profile")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationLink {
                    SettingsView()
                } label: {
                    Image(systemName: "gearshape")
                }
            }
        }
        .onAppear {
            viewModel.loadProfile()
        }
    }
}
