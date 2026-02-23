import SwiftUI

struct PostCardView: View {
    let post: PostEntity
    let author: UserEntity?
    let likeCount: Int
    let commentCount: Int
    let isLiked: Bool
    let onLike: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Header
            HStack(spacing: 12) {
                AvatarView(imagePath: author?.avatarPath, size: 36)
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(author?.username ?? "Unknown")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    Text(post.createdAt ?? Date(), style: .relative)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                
                Spacer()
            }
            .padding(.horizontal)
            .padding(.vertical, 10)
            
            // Image
            if let imagePath = post.imagePath, let image = ImageManager.shared.loadImage(named: imagePath) {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(maxHeight: 400)
                    .clipped()
            }
            
            // Actions
            HStack(spacing: 16) {
                Button(action: onLike) {
                    Image(systemName: isLiked ? "heart.fill" : "heart")
                        .font(.title3)
                        .foregroundStyle(isLiked ? .red : .primary)
                }
                
                Image(systemName: "bubble.right")
                    .font(.title3)
                    .foregroundStyle(.primary)
                
                Spacer()
            }
            .padding(.horizontal)
            .padding(.top, 10)
            
            // Like count
            if likeCount > 0 {
                Text("\(likeCount) like\(likeCount == 1 ? "" : "s")")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .padding(.horizontal)
                    .padding(.top, 4)
            }
            
            // Text
            if let text = post.text, !text.isEmpty {
                HStack(spacing: 4) {
                    Text(author?.username ?? "")
                        .fontWeight(.semibold)
                    Text(text)
                }
                .font(.subheadline)
                .padding(.horizontal)
                .padding(.top, 4)
                .lineLimit(3)
            }
            
            // Comment count
            if commentCount > 0 {
                Text("View all \(commentCount) comment\(commentCount == 1 ? "" : "s")")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .padding(.horizontal)
                    .padding(.top, 4)
            }
            
            Divider()
                .padding(.top, 12)
        }
    }
}
