import SwiftUI

struct LoginView: View {
    @ObservedObject var authVM: AuthViewModel
    @State private var username = ""
    @State private var password = ""
    @State private var isRegistering = false
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 32) {
                Spacer()
                
                Text("SocialFeed")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text(isRegistering ? "Create an account" : "Sign in to continue")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                
                VStack(spacing: 16) {
                    TextField("Username", text: $username)
                        .textFieldStyle(.roundedBorder)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                    
                    SecureField("Password", text: $password)
                        .textFieldStyle(.roundedBorder)
                }
                .padding(.horizontal, 40)
                
                if let error = authVM.error {
                    Text(error)
                        .font(.caption)
                        .foregroundStyle(.red)
                        .padding(.horizontal, 40)
                }
                
                Button {
                    Task {
                        if isRegistering {
                            await authVM.register(username: username, password: password)
                        } else {
                            await authVM.login(username: username, password: password)
                        }
                    }
                } label: {
                    if authVM.isLoading {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    } else {
                        Text(isRegistering ? "Register" : "Login")
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
                .background(.primary)
                .foregroundStyle(Color(.systemBackground))
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .padding(.horizontal, 40)
                .disabled(username.isEmpty || password.isEmpty || authVM.isLoading)
                
                Button {
                    isRegistering.toggle()
                    authVM.error = nil
                } label: {
                    Text(isRegistering ? "Already have an account? Login" : "Don't have an account? Register")
                        .font(.subheadline)
                }
                
                Spacer()
                Spacer()
            }
        }
    }
}
