using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;
using SocialFeed.Api;
using SocialFeed.Api.Data;
using SocialFeed.Api.Middleware;
using SocialFeed.Api.Services;

var builder = WebApplication.CreateBuilder(args);

// JSON camelCase
builder.Services.AddControllers()
    .AddJsonOptions(o =>
    {
        o.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
        o.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;
    });

builder.Services.AddDbContext<AppDbContext>(o =>
    o.UseSqlite(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddSingleton<AuthService>();
builder.Services.AddCors(o => o.AddDefaultPolicy(p => p.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));

var app = builder.Build();

// Create DB and seed
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.EnsureCreated();
    SeedData.Initialize(db);
}

app.UseCors();
app.UseMiddleware<ErrorHandlingMiddleware>();

// Auth middleware - extract JWT userId into HttpContext.Items
app.Use(async (context, next) =>
{
    var auth = context.RequestServices.GetRequiredService<AuthService>();
    var header = context.Request.Headers.Authorization.ToString();
    if (header.StartsWith("Bearer "))
    {
        var userId = auth.ValidateToken(header[7..]);
        if (userId != null)
            context.Items["UserId"] = userId;
    }
    await next();
});

// Serve uploads as static files
app.UseStaticFiles(new StaticFileOptions
{
    FileProvider = new Microsoft.Extensions.FileProviders.PhysicalFileProvider(
        Path.Combine(Directory.GetCurrentDirectory(), "uploads")),
    RequestPath = "/uploads"
});

app.MapControllers();

app.MapGet("/api/health", () => Results.Json(new { status = "ok", timestamp = DateTime.UtcNow.ToString("o") }));

app.Run();
