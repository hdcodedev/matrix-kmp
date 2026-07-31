import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        VStack {
            Text(GreetingKt.greeting())
                .font(.largeTitle)
        }
        .padding()
    }
}
