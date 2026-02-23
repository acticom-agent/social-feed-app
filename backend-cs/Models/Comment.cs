namespace SocialFeed.Api.Models;

public class Comment
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string Text { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public string PostId { get; set; } = string.Empty;
    public string AuthorId { get; set; } = string.Empty;

    public Post Post { get; set; } = null!;
    public User Author { get; set; } = null!;
}
