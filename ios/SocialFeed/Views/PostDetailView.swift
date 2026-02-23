import SwiftUI

struct PostDetailView: View {
    @StateObject private var viewModel: PostDetailViewModel
    
    init(post: PostEntity) {
        _viewModel = StateObject(wrappedValue: PostDetailViewModel(post: post))
    }
    
    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    // Post header
                    let author = viewModel.getAuthor(for: viewModel.post.authorId)
                    HStack(spacing: 12) {
                        AvatarView(imagePath: author?.avatarPath, size: 40)
                        VStack(alignment: .leading) {
                            Text(author?.username ?? "Unknown")
                                .fontWeight(.semibold)
                            Text(viewModel.post.createdAt ?? Date(), style: .relative)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                    }
                    .padding(.horizontal)
                    
                    // Image
                    if let imagePath = viewModel.post.imagePath,
                       let image = ImageManager.shared.loadImage(named: imagePath) {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(maxWidth: .infinity)
                    }
                    
                    // Actions
                    HStack(spacing: 16) {
                        Button { viewModel.toggleLike() } label: {
                            HStack(spacing: 4) {
                                Image(systemName: viewModel.isLiked() ? "heart.fill" : "heart")
                                    .foregroundStyle(viewModel.isLiked() ? .red : .primary)
                                Text("\(viewModel.likeCount())")
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
                    if let text = viewModel.post.text, !text.isEmpty {
                        HStack(spacing: 4) {
                            Text(author?.username ?? "")
                                .fontWeight(.semibold)
                            Text(text)
                        }
                        .font(.subheadline)
                        .padding(.horizontal)
                    }
                    
                    Divider()
                        .padding(.vertical, 8)
                    
                    // Comments
                    ForEach(viewModel.comments, id: \.objectID) { comment in
                        let commentAuthor = viewModel.getAuthor(for: comment.authorId)
                        HStack(alignment: .top, spacing: 10) {
                            AvatarView(imagePath: commentAuthor?.avatarPath, size: 28)
                            VStack(alignment: .leading, spacing: 2) {
                                HStack(spacing: 4) {
                                    Text(commentAuthor?.username ?? "Unknown")
                                        .fontWeight(.semibold)
                                    Text(comment.text ?? "")
                                }
                                .font(.subheadline)
                                
                                Text(comment.createdAt ?? Date(), style: .relative)
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
