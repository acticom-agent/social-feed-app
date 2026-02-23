using SocialFeed.Api.Data;
using SocialFeed.Api.Models;

namespace SocialFeed.Api;

public static class SeedData
{
    public static void Initialize(AppDbContext db)
    {
        if (db.Users.Any()) return;

        var hash = BCrypt.Net.BCrypt.HashPassword("password", 10);

        var alice = new User { Username = "alice", PasswordHash = hash, AvatarUrl = "https://i.pravatar.cc/150?u=alice" };
        var bob = new User { Username = "bob", PasswordHash = hash, AvatarUrl = "https://i.pravatar.cc/150?u=bob" };
        var charlie = new User { Username = "charlie", PasswordHash = hash, AvatarUrl = "https://i.pravatar.cc/150?u=charlie" };

        db.Users.AddRange(alice, bob, charlie);
        db.SaveChanges();

        var post1 = new Post { Text = "Hello world! This is my first post 🌍", AuthorId = alice.Id };
        var post2 = new Post { Text = "Beautiful sunset today 🌅", ImageUrl = "https://picsum.photos/seed/sunset/800/600", AuthorId = bob.Id };
        var post3 = new Post { Text = "Just shipped a new feature! 🚀", AuthorId = charlie.Id };

        db.Posts.AddRange(post1, post2, post3);
        db.SaveChanges();

        db.Likes.AddRange(
            new Like { PostId = post1.Id, UserId = bob.Id },
            new Like { PostId = post1.Id, UserId = charlie.Id },
            new Like { PostId = post2.Id, UserId = alice.Id }
        );
        db.SaveChanges();

        db.Comments.AddRange(
            new Comment { Text = "Welcome! 🎉", PostId = post1.Id, AuthorId = bob.Id },
            new Comment { Text = "Amazing photo!", PostId = post2.Id, AuthorId = alice.Id },
            new Comment { Text = "Congrats! 🎊", PostId = post3.Id, AuthorId = alice.Id },
            new Comment { Text = "Well done!", PostId = post3.Id, AuthorId = bob.Id }
        );
        db.SaveChanges();

        Console.WriteLine("Seeded: 3 users, 3 posts, 3 likes, 4 comments");
    }
}
