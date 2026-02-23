using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SocialFeed.Api.Data;
using SocialFeed.Api.Middleware;

namespace SocialFeed.Api.Controllers;

[ApiController]
[Route("api/users")]
public class UsersController : ControllerBase
{
    private readonly AppDbContext _db;

    public UsersController(AppDbContext db) => _db = db;

    [HttpGet("{id}")]
    public async Task<IActionResult> GetUser(string id)
    {
        var user = await _db.Users
            .Where(u => u.Id == id)
            .Select(u => new { id = u.Id, username = u.Username, avatarUrl = u.AvatarUrl, createdAt = u.CreatedAt })
            .FirstOrDefaultAsync();
        if (user == null) throw new AppException(404, "User not found");
        return Ok(user);
    }

    [HttpPut("me")]
    public async Task<IActionResult> UpdateMe([FromBody] UpdateUserRequest req)
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var user = await _db.Users.FindAsync(userId);
        if (user == null) throw new AppException(404, "User not found");

        if (req.Username != null)
        {
            if (string.IsNullOrWhiteSpace(req.Username))
                throw new AppException(400, "Username cannot be empty");
            var existing = await _db.Users.FirstOrDefaultAsync(u => u.Username == req.Username);
            if (existing != null && existing.Id != userId)
                throw new AppException(409, "Username already taken");
            user.Username = req.Username;
        }
        if (req.AvatarUrl != null)
            user.AvatarUrl = req.AvatarUrl;

        await _db.SaveChangesAsync();
        return Ok(new { id = user.Id, username = user.Username, avatarUrl = user.AvatarUrl, createdAt = user.CreatedAt });
    }
}

public class UpdateUserRequest
{
    public string? Username { get; set; }
    public string? AvatarUrl { get; set; }
}
