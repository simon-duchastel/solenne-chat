import UIKit
import SwiftUI
import SelenneComposeApp

struct ComposeView: UIViewControllerRepresentable {
    private let graph: IosApplicationGraph

    init(graph: IosApplicationGraph) {
        self.graph = graph
    }

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.makeUiViewController(graph: graph)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    private let graph: IosApplicationGraph

    init() {
        self.graph = IosApplicationGraphCompanion.shared.create()
    }

    var body: some View {
        ComposeView(graph: self.graph)
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
