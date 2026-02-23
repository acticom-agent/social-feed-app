using Microsoft.AspNetCore.Mvc;
using SocialFeed.Api.Data;
using SocialFeed.Api.Middleware;

namespace SocialFeed.Api.Controllers;

[ApiController]
[Route("api/comments")]
public class CommentsController : ControllerBase
{
    private readonly AppDbContext _db;

    public CommentsController(AppDbContext db) => _db = db;

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(string id)
    {
        var userId = HttpContext.Items["UserId"] as string;
        if (userId == null) return Unauthorized(new { error = "No token provided" });

        var comment = await _db.Comments.FindAsync(id);
        if (comment == null) throw new AppException(404, "Comment not found");
        if (comment.AuthorId != userId) throw new AppException(403, "Not authorized");

        _db.Comments.Remove(comment);
        await _db.SaveChangesAsync();
        return Ok(new { message = "Comment deleted" });
    }
}
