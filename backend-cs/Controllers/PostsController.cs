using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SocialFeed.Api.Data;
using SocialFeed.Api.Middleware;
using SocialFeed.Api.Models;

namespace SocialFeed.Api.Controllers;

[ApiController]
[Route("api/posts")]
public class PostsController : ControllerBase
{
    private readonly AppDbContext _db;

    public PostsController(AppDbContext db) => _db = db;

    [HttpGet]
    public async Task<IActionResult> GetPosts([FromQuery] int limit = 20, [FromQuery] int offset = 0)
    {
        limit = Math.Min(Math.Max(limit, 1), 100);
        offset = Math.Max(offset, 0);

        var posts = await _db.Posts
            .OrderByDescending(p => p.CreatedAt)
            .Skip(offset).Take(limit)
            .Select(p => new
            {
                id = p.Id,
                text = p.Text,
                imageUrl = p.ImageUrl,
                createdAt = p.CreatedAt,
                authorId = p.AuthorId,
                author = new { id = p.Author.Id, username = p.Author.Username, avatarUrl = p.Author.AvatarUrl },
                _count = new { likes = p.Likes.Count, comments = p.Comments.Count }
            }).ToListAsync();

        var total = await _db.Posts.CountAsync();
        return Ok(new { posts, total, limit, offset });
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetPost(string id)
    {
        var post = await _db.Posts
            .Where(p => p.Id == id)
            .Select(p => new
            {
                id = p.Id,
                text = p.Text,
                imageUrl = p.ImageUrl,
                createdAt = p.CreatedAt,
                authorId = p.AuthorId,
                author = new { id = p.Author.Id, username = p.Author.Username, avatarUrl = p.Author.AvatarUrl },
                comments = p.Comments.OrderBy(c => c.CreatedAt).Select(c => new
                {
                    id = c.Id,
                    text = c.Text,
                    createdAt = c.CreatedAt,
                    postId = c.PostId,
                    authorId = c.AuthorId,
                    author = new { id = c.Author.Id, username = c.Author.Username, avatarUrl = c.Author.AvatarUrl }
                }).ToList(),
                _count = new { likes = p.Likes.Count }
            }).FirstOrDefaultAsync();

        if (post == null) throw new AppException(404, "Post not found");
        return Ok(post);
    }

    [HttpPost]
    public async Task<IActionResult> CreatePost()
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var text = Request.Form["text"].ToString();
        if (string.IsNullOrWhiteSpace(text))
            throw new AppException(400, "Text is required");

        string? imageUrl = null;
        if (Request.Form.Files.Count > 0)
        {
            var file = Request.Form.Files["image"];
            if (file != null)
            {
                var ext = Path.GetExtension(file.FileName).ToLower();
                var allowed = new[] { ".jpg", ".jpeg", ".png", ".gif", ".webp" };
                if (!allowed.Contains(ext))
                    throw new AppException(400, "Only image files are allowed");

                var filename = $"{Guid.NewGuid()}{ext}";
                var uploadsDir = Path.Combine(Directory.GetCurrentDirectory(), "uploads");
                Directory.CreateDirectory(uploadsDir);
                var filePath = Path.Combine(uploadsDir, filename);
                using var stream = new FileStream(filePath, FileMode.Create);
                await file.CopyToAsync(stream);
                imageUrl = $"/uploads/{filename}";
            }
        }

        var post = new Post
        {
            Text = text.Trim(),
            ImageUrl = imageUrl,
            AuthorId = userId
        };
        _db.Posts.Add(post);
        await _db.SaveChangesAsync();

        var author = await _db.Users.Where(u => u.Id == userId)
            .Select(u => new { id = u.Id, username = u.Username, avatarUrl = u.AvatarUrl })
            .FirstAsync();

        return StatusCode(201, new
        {
            id = post.Id,
            text = post.Text,
            imageUrl = post.ImageUrl,
            createdAt = post.CreatedAt,
            authorId = post.AuthorId,
            author
        });
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeletePost(string id)
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var post = await _db.Posts.FindAsync(id);
        if (post == null) throw new AppException(404, "Post not found");
        if (post.AuthorId != userId) throw new AppException(403, "Not authorized");

        _db.Posts.Remove(post);
        await _db.SaveChangesAsync();
        return Ok(new { message = "Post deleted" });
    }

    // Likes
    [HttpPost("{id}/like")]
    public async Task<IActionResult> ToggleLike(string id)
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var post = await _db.Posts.FindAsync(id);
        if (post == null) throw new AppException(404, "Post not found");

        var existing = await _db.Likes.FirstOrDefaultAsync(l => l.PostId == id && l.UserId == userId);
        if (existing != null)
        {
            _db.Likes.Remove(existing);
            await _db.SaveChangesAsync();
            return Ok(new { liked = false });
        }

        _db.Likes.Add(new Like { PostId = id, UserId = userId });
        await _db.SaveChangesAsync();
        return Ok(new { liked = true });
    }

    [HttpGet("{id}/likes")]
    public async Task<IActionResult> GetLikes(string id)
    {
        var userId = HttpContext.Items["UserId"] as string;
        var count = await _db.Likes.CountAsync(l => l.PostId == id);
        var liked = false;
        if (userId != null)
            liked = await _db.Likes.AnyAsync(l => l.PostId == id && l.UserId == userId);
        return Ok(new { count, liked });
    }

    // Comments
    [HttpGet("{id}/comments")]
    public async Task<IActionResult> GetComments(string id, [FromQuery] int limit = 20, [FromQuery] int offset = 0)
    {
        limit = Math.Min(Math.Max(limit, 1), 100);
        offset = Math.Max(offset, 0);

        var comments = await _db.Comments
            .Where(c => c.PostId == id)
            .OrderBy(c => c.CreatedAt)
            .Skip(offset).Take(limit)
            .Select(c => new
            {
                id = c.Id,
                text = c.Text,
                createdAt = c.CreatedAt,
                postId = c.PostId,
                authorId = c.AuthorId,
                author = new { id = c.Author.Id, username = c.Author.Username, avatarUrl = c.Author.AvatarUrl }
            }).ToListAsync();

        var total = await _db.Comments.CountAsync(c => c.PostId == id);
        return Ok(new { comments, total, limit, offset });
    }

    [HttpPost("{id}/comments")]
    public async Task<IActionResult> CreateComment(string id, [FromBody] CreateCommentRequest req)
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        if (string.IsNullOrWhiteSpace(req.Text))
            throw new AppException(400, "Text is required");

        var post = await _db.Posts.FindAsync(id);
        if (post == null) throw new AppException(404, "Post not found");

        var comment = new Comment
        {
            Text = req.Text.Trim(),
            PostId = id,
            AuthorId = userId
        };
        _db.Comments.Add(comment);
        await _db.SaveChangesAsync();

        var author = await _db.Users.Where(u => u.Id == userId)
            .Select(u => new { id = u.Id, username = u.Username, avatarUrl = u.AvatarUrl })
            .FirstAsync();

        return StatusCode(201, new
        {
            id = comment.Id,
            text = comment.Text,
            createdAt = comment.CreatedAt,
            postId = comment.PostId,
            authorId = comment.AuthorId,
            author
        });
    }
}

public class CreateCommentRequest
{
    public string? Text { get; set; }
}
