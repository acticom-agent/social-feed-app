import SwiftUI

@main
struct SocialFeedApp: App {
    let persistenceController = PersistenceController.shared
    @AppStorage("hasCompletedSetup") private var hasCompletedSetup = false

    var body: some Scene {
        WindowGroup {
            if hasCompletedSetup {
                ContentView()
                    .environment(\.managedObjectContext, persistenceController.container.viewContext)
            } else {
                ProfileSetupView()
                    .environment(\.managedObjectContext, persistenceController.container.viewContext)
            }
        }
    }
}
