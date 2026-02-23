namespace SocialFeed.Api.Models;

public class Like
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public string PostId { get; set; } = string.Empty;
    public string UserId { get; set; } = string.Empty;

    public Post Post { get; set; } = null!;
    public User User { get; set; } = null!;
}
