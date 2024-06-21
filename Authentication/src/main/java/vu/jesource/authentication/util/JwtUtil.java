package vu.jesource.authentication.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vu.jesource.authentication.web.models.User;

import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipException;

@Component
public class JwtUtil {
    private final static long expirationTimeInMs = 60 * 60 * 1000; // 1 hour (in milliseconds)

    @Value("${jwt.encoding.key}")
    private String keyString;

    @Value("${jwt.issuer}")
    private String issuer;

    public static boolean isTokenValid(Jws<Claims> claims) {
        Date now = new Date();
        Claims body = claims.getBody();

        boolean isNotExpired = body.getExpiration().after(now);
        boolean isActive = body.getIssuedAt().before(now);
        boolean isEnabled = body.getNotBefore().before(now);

        return isNotExpired && isActive && isEnabled;
    }

    public String generateJwtForUser(User user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(issuer)
                .setSubject("Authentication token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMs))
                .setNotBefore(new Date(System.currentTimeMillis()))
                .claim("user", mapper.writeValueAsString(user))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(keyString))
                .compact();
    }

    public Jws<Claims> extractClaimsFromToken(String jwt) throws MalformedJwtException, ZipException {
        return Jwts.parserBuilder()
                .setSigningKey(keyString)
                .build()
                .parseClaimsJws(jwt);
    }

    public static User extractUserFromClaims(Jws<Claims> jws) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                jws.getBody().get("user", String.class),    // get 'user' claim
                User.class  // map it to the User class
        );
    }
}
