import CoreData

struct PersistenceController {
    static let shared = PersistenceController()

    let container: NSPersistentContainer

    init(inMemory: Bool = false) {
        // Build Core Data model programmatically
        let model = NSManagedObjectModel()

        // User Entity
        let userEntity = NSEntityDescription()
        userEntity.name = "UserEntity"
        userEntity.managedObjectClassName = "UserEntity"
        
        let userId = NSAttributeDescription()
        userId.name = "id"
        userId.attributeType = .UUIDAttributeType
        
        let username = NSAttributeDescription()
        username.name = "username"
        username.attributeType = .stringAttributeType
        
        let avatarPath = NSAttributeDescription()
        avatarPath.name = "avatarPath"
        avatarPath.attributeType = .stringAttributeType
        avatarPath.isOptional = true
        
        userEntity.properties = [userId, username, avatarPath]

        // Post Entity
        let postEntity = NSEntityDescription()
        postEntity.name = "PostEntity"
        postEntity.managedObjectClassName = "PostEntity"
        
        let postId = NSAttributeDescription()
        postId.name = "id"
        postId.attributeType = .UUIDAttributeType
        
        let authorId = NSAttributeDescription()
        authorId.name = "authorId"
        authorId.attributeType = .UUIDAttributeType
        
        let postText = NSAttributeDescription()
        postText.name = "text"
        postText.attributeType = .stringAttributeType
        
        let imagePath = NSAttributeDescription()
        imagePath.name = "imagePath"
        imagePath.attributeType = .stringAttributeType
        imagePath.isOptional = true
        
        let createdAt = NSAttributeDescription()
        createdAt.name = "createdAt"
        createdAt.attributeType = .dateAttributeType
        
        postEntity.properties = [postId, authorId, postText, imagePath, createdAt]

        // Like Entity
        let likeEntity = NSEntityDescription()
        likeEntity.name = "LikeEntity"
        likeEntity.managedObjectClassName = "LikeEntity"
        
        let likePostId = NSAttributeDescription()
        likePostId.name = "postId"
        likePostId.attributeType = .UUIDAttributeType
        
        let likeUserId = NSAttributeDescription()
        likeUserId.name = "userId"
        likeUserId.attributeType = .UUIDAttributeType
        
        likeEntity.properties = [likePostId, likeUserId]

        // Comment Entity
        let commentEntity = NSEntityDescription()
        commentEntity.name = "CommentEntity"
        commentEntity.managedObjectClassName = "CommentEntity"
        
        let commentId = NSAttributeDescription()
        commentId.name = "id"
        commentId.attributeType = .UUIDAttributeType
        
        let commentPostId = NSAttributeDescription()
        commentPostId.name = "postId"
        commentPostId.attributeType = .UUIDAttributeType
        
        let commentAuthorId = NSAttributeDescription()
        commentAuthorId.name = "authorId"
        commentAuthorId.attributeType = .UUIDAttributeType
        
        let commentText = NSAttributeDescription()
        commentText.name = "text"
        commentText.attributeType = .stringAttributeType
        
        let commentCreatedAt = NSAttributeDescription()
        commentCreatedAt.name = "createdAt"
        commentCreatedAt.attributeType = .dateAttributeType
        
        commentEntity.properties = [commentId, commentPostId, commentAuthorId, commentText, commentCreatedAt]

        model.entities = [userEntity, postEntity, likeEntity, commentEntity]

        container = NSPersistentContainer(name: "SocialFeed", managedObjectModel: model)
        
        if inMemory {
            container.persistentStoreDescriptions.first!.url = URL(fileURLWithPath: "/dev/null")
        }
        
        container.loadPersistentStores { _, error in
            if let error = error as NSError? {
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        }
        container.viewContext.automaticallyMergesChangesFromParent = true
        container.viewContext.mergePolicy = NSMergeByPropertyObjectTrumpMergePolicy
    }

    func clearAllData() {
        let context = container.viewContext
        for entityName in ["UserEntity", "PostEntity", "LikeEntity", "CommentEntity"] {
            let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: entityName)
            let batchDelete = NSBatchDeleteRequest(fetchRequest: fetchRequest)
            try? context.execute(batchDelete)
        }
        try? context.save()
    }
}

// MARK: - NSManagedObject Subclasses

@objc(UserEntity)
public class UserEntity: NSManagedObject {
    @NSManaged public var id: UUID?
    @NSManaged public var username: String?
    @NSManaged public var avatarPath: String?
}

@objc(PostEntity)
public class PostEntity: NSManagedObject {
    @NSManaged public var id: UUID?
    @NSManaged public var authorId: UUID?
    @NSManaged public var text: String?
    @NSManaged public var imagePath: String?
    @NSManaged public var createdAt: Date?
}

@objc(LikeEntity)
public class LikeEntity: NSManagedObject {
    @NSManaged public var postId: UUID?
    @NSManaged public var userId: UUID?
}

@objc(CommentEntity)
public class CommentEntity: NSManagedObject {
    @NSManaged public var id: UUID?
    @NSManaged public var postId: UUID?
    @NSManaged public var authorId: UUID?
    @NSManaged public var text: String?
    @NSManaged public var createdAt: Date?
}
