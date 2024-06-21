package vu.jesource.authentication.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vu.jesource.authentication.util.JwtUtil;
import vu.jesource.authentication.web.exceptions.LoginException;
import vu.jesource.authentication.web.exceptions.TokenProcessingException;
import vu.jesource.authentication.web.models.User;

import java.util.zip.ZipException;

import static vu.jesource.authentication.util.JwtUtil.extractUserFromClaims;

@Service
public class JwtService {
    private final JwtUtil jwtUtil;
    private final UserService userService;


    public JwtService(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    public ResponseEntity<String> getJwtValidationResponse(String jwt) {
        try {

            return getResponseEntity(jwt);

        } catch (MalformedJwtException | JsonProcessingException | ZipException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> getResponseEntity(String jwt) throws ZipException, JsonProcessingException {
        Jws<Claims> claims = jwtUtil.extractClaimsFromToken(jwt);

        if (JwtUtil.isTokenValid(claims)) {
            if (userService.doesUserExistAndPasswordIsCorrect(extractUserFromClaims(claims), true)) {
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public String generateJwtOnLogin(User user) throws LoginException {
        User dbUser = userService.getUserWhoTriesToLogin(user);

        try {
            return jwtUtil.generateJwtForUser(dbUser);
        } catch (JsonProcessingException e) {
            throw new LoginException("Failed to generate token");
        }
    }

    public User getUserDataFromToken(String jwt) throws TokenProcessingException {
        try {
            return extractUserFromClaims(jwtUtil.extractClaimsFromToken(jwt));
        } catch (JsonProcessingException | ZipException e) {
            throw new TokenProcessingException(e.getMessage());
        }
    }
}
