using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace SocialFeed.Api.Services;

public class AuthService
{
    private readonly string _secret;
    private readonly int _expiresInDays;

    public AuthService(IConfiguration config)
    {
        _secret = config["Jwt:Secret"] ?? "dev-secret";
        _expiresInDays = int.Parse(config["Jwt:ExpiresInDays"] ?? "7");
    }

    public string GenerateToken(string userId)
    {
        var keyBytes = PadKey(Encoding.UTF8.GetBytes(_secret), 32);
        var key = new SymmetricSecurityKey(keyBytes);
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        var token = new JwtSecurityToken(
            claims: new[] { new Claim("userId", userId) },
            expires: DateTime.UtcNow.AddDays(_expiresInDays),
            signingCredentials: creds
        );
        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public string? ValidateToken(string token)
    {
        try
        {
            var key = PadKey(Encoding.UTF8.GetBytes(_secret), 32);
            var handler = new JwtSecurityTokenHandler();
            var principal = handler.ValidateToken(token, new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
            }, out _);
            return principal.FindFirst("userId")?.Value;
        }
        catch
        {
            return null;
        }
    }

    private static byte[] PadKey(byte[] key, int minLength)
    {
        if (key.Length >= minLength) return key;
        var padded = new byte[minLength];
        Array.Copy(key, padded, key.Length);
        return padded;
    }

    public string HashPassword(string password) => BCrypt.Net.BCrypt.HashPassword(password, 10);
    public bool VerifyPassword(string password, string hash) => BCrypt.Net.BCrypt.Verify(password, hash);
}
