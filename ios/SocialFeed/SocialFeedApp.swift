import SwiftUI

@main
struct SocialFeedApp: App {
    @StateObject private var authVM = AuthViewModel.shared

    var body: some Scene {
        WindowGroup {
            if authVM.isAuthenticated {
                ContentView()
                    .environmentObject(authVM)
            } else {
                LoginView(authVM: authVM)
            }
        }
    }
}
