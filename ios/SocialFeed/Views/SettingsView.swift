import SwiftUI

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @AppStorage("hasCompletedSetup") private var hasCompletedSetup = false
    
    var body: some View {
        List {
            Section("Data") {
                Button {
                    viewModel.exportedJSON = viewModel.exportAsJSON()
                    viewModel.showExportSheet = true
                } label: {
                    Label("Export as JSON", systemImage: "square.and.arrow.up")
                }
                
                Button(role: .destructive) {
                    viewModel.showClearConfirmation = true
                } label: {
                    Label("Clear All Data", systemImage: "trash")
                }
            }
            
            Section("Account") {
                Button(role: .destructive) {
                    viewModel.showResetConfirmation = true
                } label: {
                    Label("Reset Profile", systemImage: "person.crop.circle.badge.minus")
                }
            }
            
            Section("About") {
                HStack {
                    Text("Version")
                    Spacer()
                    Text("1.0.0")
                        .foregroundStyle(.secondary)
                }
                HStack {
                    Text("App")
                    Spacer()
                    Text("SocialFeed")
                        .foregroundStyle(.secondary)
                }
            }
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Clear All Data?", isPresented: $viewModel.showClearConfirmation) {
            Button("Cancel", role: .cancel) {}
            Button("Clear", role: .destructive) {
                viewModel.clearAllData()
            }
        } message: {
            Text("This will delete all posts, comments, and likes. This cannot be undone.")
        }
        .alert("Reset Profile?", isPresented: $viewModel.showResetConfirmation) {
            Button("Cancel", role: .cancel) {}
            Button("Reset", role: .destructive) {
                viewModel.resetProfile()
                hasCompletedSetup = false
            }
        } message: {
            Text("This will delete all data and return to the setup screen.")
        }
        .sheet(isPresented: $viewModel.showExportSheet) {
            NavigationStack {
                ScrollView {
                    Text(viewModel.exportedJSON)
                        .font(.system(.caption, design: .monospaced))
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .navigationTitle("Exported Data")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Done") {
                            viewModel.showExportSheet = false
                        }
                    }
                    ToolbarItem(placement: .topBarLeading) {
                        ShareLink(item: viewModel.exportedJSON) {
                            Image(systemName: "square.and.arrow.up")
                        }
                    }
                }
            }
        }
    }
}
