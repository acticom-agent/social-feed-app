import SwiftUI

struct ContentView: View {
    @Environment(\.managedObjectContext) private var viewContext
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack {
                FeedView()
            }
            .tabItem {
                Label("Feed", systemImage: "house.fill")
            }
            .tag(0)

            NavigationStack {
                CreatePostView()
            }
            .tabItem {
                Label("Create", systemImage: "plus.square.fill")
            }
            .tag(1)

            NavigationStack {
                ProfileView()
            }
            .tabItem {
                Label("Profile", systemImage: "person.fill")
            }
            .tag(2)
        }
        .tint(.primary)
    }
}
