using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SocialFeed.Api.Data;
using SocialFeed.Api.Middleware;
using SocialFeed.Api.Models;
using SocialFeed.Api.Services;

namespace SocialFeed.Api.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly AuthService _auth;

    public AuthController(AppDbContext db, AuthService auth)
    {
        _db = db;
        _auth = auth;
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] AuthRequest req)
    {
        if (string.IsNullOrWhiteSpace(req.Username) || string.IsNullOrWhiteSpace(req.Password))
            throw new AppException(400, "Username and password required");
        if (req.Password.Length < 4)
            throw new AppException(400, "Password must be at least 4 characters");

        var existing = await _db.Users.FirstOrDefaultAsync(u => u.Username == req.Username);
        if (existing != null) throw new AppException(409, "Username already taken");

        var user = new User
        {
            Username = req.Username,
            PasswordHash = _auth.HashPassword(req.Password)
        };
        _db.Users.Add(user);
        await _db.SaveChangesAsync();

        var token = _auth.GenerateToken(user.Id);
        return StatusCode(201, new
        {
            token,
            user = new { id = user.Id, username = user.Username, avatarUrl = user.AvatarUrl, createdAt = user.CreatedAt }
        });
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] AuthRequest req)
    {
        if (string.IsNullOrWhiteSpace(req.Username) || string.IsNullOrWhiteSpace(req.Password))
            throw new AppException(400, "Username and password required");

        var user = await _db.Users.FirstOrDefaultAsync(u => u.Username == req.Username);
        if (user == null) throw new AppException(401, "Invalid credentials");

        if (!_auth.VerifyPassword(req.Password, user.PasswordHash))
            throw new AppException(401, "Invalid credentials");

        var token = _auth.GenerateToken(user.Id);
        return Ok(new
        {
            token,
            user = new { id = user.Id, username = user.Username, avatarUrl = user.AvatarUrl, createdAt = user.CreatedAt }
        });
    }

    [HttpGet("me")]
    public async Task<IActionResult> Me()
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var user = await _db.Users
            .Where(u => u.Id == userId)
            .Select(u => new { id = u.Id, username = u.Username, avatarUrl = u.AvatarUrl, createdAt = u.CreatedAt })
            .FirstOrDefaultAsync();
        if (user == null) throw new AppException(404, "User not found");
        return Ok(user);
    }
}

public class AuthRequest
{
    public string? Username { get; set; }
    public string? Password { get; set; }
}
