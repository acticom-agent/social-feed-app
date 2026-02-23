import SwiftUI

struct AvatarView: View {
    let imagePath: String?
    var size: CGFloat = 40
    
    var body: some View {
        Group {
            if let path = imagePath, let image = ImageManager.shared.loadImage(named: path) {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
            } else {
                Image(systemName: "person.circle.fill")
                    .resizable()
                    .foregroundStyle(.secondary)
            }
        }
        .frame(width: size, height: size)
        .clipShape(Circle())
    }
}
