package event.notificator.config;

import event.notificator.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenManager {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenManager.class);
    private final SecretKey secretKey;

    public JwtTokenManager(
            @Value("${jwt.secret-key}") String keyString) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
    }

    public String getLoginFromToken(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (Exception e) {
            logger.error("Failed to get login from token: {}", e.getMessage(), e);
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            return parseToken(token).get("userId", Long.class);
        } catch (Exception e) {
            logger.error("Failed to get userId from token: {}", e.getMessage(), e);
            return null;
        }
    }

    public Role getRoleFromToken(String token) {
        try {
            String roleString = parseToken(token).get("role", String.class);
            if (roleString != null) {
                return Role.valueOf(roleString);
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get role from token: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Map<String, Object> getAllClaims(String token) {
        try {
            Claims claims = parseToken(token);
            Map<String, Object> result = new HashMap<>();
            for (String key : claims.keySet()) {
                result.put(key, claims.get(key));
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to get all claims: {}", e.getMessage(), e);
            return Map.of();
        }
    }
}
