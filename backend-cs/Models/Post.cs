namespace SocialFeed.Api.Models;

public class Post
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string Text { get; set; } = string.Empty;
    public string? ImageUrl { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public string AuthorId { get; set; } = string.Empty;

    public User Author { get; set; } = null!;
    public List<Like> Likes { get; set; } = new();
    public List<Comment> Comments { get; set; } = new();
}
