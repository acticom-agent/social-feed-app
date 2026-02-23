import SwiftUI
import PhotosUI

struct CreatePostView: View {
    @StateObject private var viewModel = CreatePostViewModel()
    
    var body: some View {
        VStack(spacing: 16) {
            TextEditor(text: $viewModel.text)
                .frame(minHeight: 100)
                .padding(8)
                .background(Color(.secondarySystemBackground))
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .overlay(
                    Group {
                        if viewModel.text.isEmpty {
                            Text("What's on your mind?")
                                .foregroundStyle(.tertiary)
                                .padding(.leading, 12)
                                .padding(.top, 16)
                        }
                    },
                    alignment: .topLeading
                )
            
            Spacer()
            
            Button {
                viewModel.createPost()
            } label: {
                Text("Share Post")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(.primary)
                    .foregroundStyle(Color(.systemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .disabled(viewModel.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
        }
        .padding()
        .navigationTitle("New Post")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Posted!", isPresented: $viewModel.didPost) {
            Button("OK") { viewModel.reset() }
        } message: {
            Text("Your post has been shared.")
        }
    }
}
