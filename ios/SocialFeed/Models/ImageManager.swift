import UIKit
import SwiftUI

final class ImageManager {
    static let shared = ImageManager()
    
    private let fileManager = FileManager.default
    
    private var imagesDirectory: URL {
        let docs = fileManager.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let dir = docs.appendingPathComponent("Images", isDirectory: true)
        if !fileManager.fileExists(atPath: dir.path) {
            try? fileManager.createDirectory(at: dir, withIntermediateDirectories: true)
        }
        return dir
    }
    
    func saveImage(_ image: UIImage, name: String? = nil) -> String? {
        let fileName = name ?? UUID().uuidString + ".jpg"
        let url = imagesDirectory.appendingPathComponent(fileName)
        guard let data = image.jpegData(compressionQuality: 0.8) else { return nil }
        do {
            try data.write(to: url)
            return fileName
        } catch {
            print("Error saving image: \(error)")
            return nil
        }
    }
    
    func loadImage(named fileName: String) -> UIImage? {
        let url = imagesDirectory.appendingPathComponent(fileName)
        guard let data = try? Data(contentsOf: url) else { return nil }
        return UIImage(data: data)
    }
    
    func deleteImage(named fileName: String) {
        let url = imagesDirectory.appendingPathComponent(fileName)
        try? fileManager.removeItem(at: url)
    }
    
    func deleteAllImages() {
        try? fileManager.removeItem(at: imagesDirectory)
    }
}
