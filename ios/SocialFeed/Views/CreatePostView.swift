import SwiftUI
import PhotosUI

struct CreatePostView: View {
    @StateObject private var viewModel = CreatePostViewModel()
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        VStack(spacing: 16) {
            // Text input
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
            
            // Selected image preview
            if let image = viewModel.selectedImage {
                ZStack(alignment: .topTrailing) {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 200)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    
                    Button {
                        viewModel.selectedImage = nil
                        viewModel.selectedItem = nil
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundStyle(.white)
                            .shadow(radius: 2)
                    }
                    .padding(8)
                }
            }
            
            // Image picker
            PhotosPicker(selection: $viewModel.selectedItem, matching: .images) {
                Label("Add Photo", systemImage: "photo.on.rectangle")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .onChange(of: viewModel.selectedItem) {
                Task { await viewModel.loadImage() }
            }
            
            Spacer()
            
            // Post button
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
            .disabled(viewModel.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && viewModel.selectedImage == nil)
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
