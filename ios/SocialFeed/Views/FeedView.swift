import SwiftUI

struct FeedView: View {
    @StateObject private var viewModel = FeedViewModel()
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                if viewModel.posts.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "photo.on.rectangle.angled")
                            .font(.system(size: 60))
                            .foregroundStyle(.secondary)
                        Text("No posts yet")
                            .font(.title3)
                            .foregroundStyle(.secondary)
                        Text("Create your first post!")
                            .font(.subheadline)
                            .foregroundStyle(.tertiary)
                    }
                    .padding(.top, 100)
                } else {
                    ForEach(viewModel.posts) { post in
                        NavigationLink {
                            PostDetailView(post: post)
                        } label: {
                            PostCardView(
                                post: post,
                                likeCount: viewModel.likeCount(for: post),
                                commentCount: viewModel.commentCount(for: post),
                                isLiked: viewModel.isLiked(post),
                                onLike: { viewModel.toggleLike(post) }
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
        .navigationTitle("SocialFeed")
        .navigationBarTitleDisplayMode(.inline)
        .refreshable {
            viewModel.loadPosts()
        }
        .onAppear {
            viewModel.loadPosts()
        }
    }
}
