import SwiftUI

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var showResetConfirmation = false
    
    func logout() {
        AuthViewModel.shared.logout()
    }
}
