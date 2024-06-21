package vu.jesource.authentication.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vu.jesource.authentication.web.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static vu.jesource.authentication.util.JwtUtil.extractUserFromClaims;
import static vu.jesource.authentication.util.JwtUtil.isTokenValid;


@SpringBootTest
class JwtUtilTest {
    private static String jwt;
    @Autowired
    private JwtUtil jwtUtil;
    private static final User testUser = new User(
            1234567890321312321L, "username",
            "encoded password", "snail@mail.com"
    );


    @BeforeEach
    void beforeEach() {
        try {
            jwt = jwtUtil.generateJwtForUser(testUser);
        } catch (JsonProcessingException e) {
            Assertions.fail(e.getMessage());
        }
    }


    @Test @SneakyThrows
    void extractUserFromJwtAndCompareItToInitialUser() {
        User user = extractUserFromClaims(jwtUtil.extractClaimsFromToken(jwt));

        assertEquals(testUser, user);
    }

    @Test @SneakyThrows
    void tokenValidityCheck() {
        Jws<Claims> claims = jwtUtil.extractClaimsFromToken(jwt);

        assertTrue(isTokenValid(claims));
    }
}