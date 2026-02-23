import SwiftUI

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: APIUser?
    @Published var isLoading = false
    @Published var error: String?
    
    static let shared = AuthViewModel()
    
    private let api = APIService.shared
    
    init() {
        if api.token != nil {
            isAuthenticated = true
            Task { await checkAuth() }
        }
    }
    
    func checkAuth() async {
        guard api.token != nil else {
            isAuthenticated = false
            return
        }
        do {
            currentUser = try await api.me()
            isAuthenticated = true
        } catch {
            // Token invalid
            api.logout()
            isAuthenticated = false
        }
    }
    
    func login(username: String, password: String) async {
        isLoading = true
        error = nil
        do {
            let resp = try await api.login(username: username, password: password)
            currentUser = resp.user
            isAuthenticated = true
        } catch {
            self.error = error.localizedDescription
        }
        isLoading = false
    }
    
    func register(username: String, password: String) async {
        isLoading = true
        error = nil
        do {
            let resp = try await api.register(username: username, password: password)
            currentUser = resp.user
            isAuthenticated = true
        } catch {
            self.error = error.localizedDescription
        }
        isLoading = false
    }
    
    func logout() {
        api.logout()
        currentUser = nil
        isAuthenticated = false
    }
}
