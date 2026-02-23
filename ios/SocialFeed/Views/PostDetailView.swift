import SwiftUI

struct PostDetailView: View {
    @StateObject private var viewModel: PostDetailViewModel
    
    init(post: APIPost) {
        _viewModel = StateObject(wrappedValue: PostDetailViewModel(post: post))
    }
    
    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    // Post header
                    HStack(spacing: 12) {
                        AvatarView(avatarUrl: viewModel.post.author.avatarUrl, size: 40)
                        VStack(alignment: .leading) {
                            Text(viewModel.post.author.username)
                                .fontWeight(.semibold)
                            Text(viewModel.post.createdAtDate, style: .relative)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                    }
                    .padding(.horizontal)
                    
                    // Image
                    if let imageUrl = viewModel.post.imageUrl, let url = URL(string: imageUrl) {
                        AsyncImage(url: url) { image in
                            image.resizable().scaledToFit()
                        } placeholder: {
                            ProgressView()
                        }
                        .frame(maxWidth: .infinity)
                    }
                    
                    // Actions
                    HStack(spacing: 16) {
                        Button { viewModel.toggleLike() } label: {
                            HStack(spacing: 4) {
                                Image(systemName: viewModel.liked ? "heart.fill" : "heart")
                                    .foregroundStyle(viewModel.liked ? .red : .primary)
                                Text("\(viewModel.currentLikeCount)")
                            }
                        }
                        .buttonStyle(.plain)
                        
                        HStack(spacing: 4) {
                            Image(systemName: "bubble.right")
                            Text("\(viewModel.comments.count)")
                        }
                        .foregroundStyle(.secondary)
                        
                        Spacer()
                    }
                    .padding(.horizontal)
                    
                    // Text
                    if !viewModel.post.text.isEmpty {
                        HStack(spacing: 4) {
                            Text(viewModel.post.author.username)
                                .fontWeight(.semibold)
                            Text(viewModel.post.text)
                        }
                        .font(.subheadline)
                        .padding(.horizontal)
                    }
                    
                    Divider()
                        .padding(.vertical, 8)
                    
                    // Comments
                    ForEach(viewModel.comments) { comment in
                        HStack(alignment: .top, spacing: 10) {
                            AvatarView(avatarUrl: comment.author.avatarUrl, size: 28)
                            VStack(alignment: .leading, spacing: 2) {
                                HStack(spacing: 4) {
                                    Text(comment.author.username)
                                        .fontWeight(.semibold)
                                    Text(comment.text)
                                }
                                .font(.subheadline)
                                
                                Text(comment.createdAtDate, style: .relative)
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                            }
                            Spacer()
                        }
                        .padding(.horizontal)
                    }
                }
                .padding(.top, 8)
            }
            
            // Comment input
            Divider()
            HStack(spacing: 12) {
                TextField("Add a comment...", text: $viewModel.newCommentText)
                    .textFieldStyle(.roundedBorder)
                
                Button("Post") {
                    viewModel.addComment()
                }
                .fontWeight(.semibold)
                .disabled(viewModel.newCommentText.trimmingCharacters(in: .whitespaces).isEmpty)
            }
            .padding()
        }
        .navigationTitle("Post")
        .navigationBarTitleDisplayMode(.inline)
    }
}
