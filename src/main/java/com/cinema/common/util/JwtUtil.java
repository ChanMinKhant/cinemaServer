package com.cinema.common.util;

import io.jsonwebtoken.*;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class JwtUtil {

    // Use a longer key and convert to a Key object to avoid Base64 decoding issues
    private static final String SECRET_STRING = "your-super-long-and-very-secure-secret-key-that-is-at-least-256-bits";
    private static final long EXP = 24 * 60 * 60 * 1000; // 1 day
    
    private static final Key SIGNING_KEY = new SecretKeySpec(
            SECRET_STRING.getBytes(StandardCharsets.UTF_8), 
            SignatureAlgorithm.HS256.getJcaName()
    );

    @SuppressWarnings("deprecation")
	public static String generateToken(int userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    @SuppressWarnings("deprecation")
	public static Claims validate(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}