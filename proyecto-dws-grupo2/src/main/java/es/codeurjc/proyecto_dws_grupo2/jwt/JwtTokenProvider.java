package es.codeurjc.proyecto_dws_grupo2.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build();

    public String tokenStringFromHeaders(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);

        // Si la cabecera existe y empieza por "Bearer ", devolvemos el token limpio
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null; // Si no hay token, devolvemos null tranquilamente
    }

    private String tokenStringFromCookies(HttpServletRequest request) {
        var cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TokenType.ACCESS.cookieName.equals(cookie.getName())) {
                    String accessToken = cookie.getValue();
                    if (accessToken != null && !accessToken.isEmpty()) {
                        return accessToken;
                    }
                }
            }
        }
        return null;
    }

    public Claims validateToken(HttpServletRequest req, boolean fromCookie) {
        var token = fromCookie
                ? tokenStringFromCookies(req)
                : tokenStringFromHeaders(req);

        // If there is no token (anonymous user), return null
        if (token == null) {
            return null;
        }

        try {
            // Try validate the token
            return validateToken(token);
        } catch (Exception e) {
            // If the token has expired or is false, return null
            return null;
        }
    }

    public Claims validateToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(TokenType.ACCESS, userDetails).compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(TokenType.REFRESH, userDetails).compact();
    }

    private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {

        var currentDate = new Date();
        var expiryDate = Date.from(currentDate.toInstant().plus(tokenType.duration));

        return Jwts.builder()
                .claim("roles", userDetails.getAuthorities())
                .claim("type", tokenType.name())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(jwtSecret);
    }
}