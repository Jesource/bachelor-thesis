package vu.jesource.authentication.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vu.jesource.authentication.web.exceptions.LoginException;
import vu.jesource.authentication.web.exceptions.RegistrationException;
import vu.jesource.authentication.web.models.User;
import vu.jesource.authentication.web.services.JwtService;
import vu.jesource.authentication.web.services.RequestProcessingService;
import vu.jesource.authentication.web.services.UserService;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RequestProcessingService requestService;

    @Autowired
    public AuthenticationController(UserService userService, JwtService jwtService, RequestProcessingService requestService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.requestService = requestService;
    }

    @GetMapping("/hi")
    public static String sayHello() {
        log.debug("Debug level");
        log.info("Attempted to access the sayHello endpoint");
        log.warn("Warn level");
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        log.info("Attempted to access register endpoint");
        try {
            User res = userService.registerUser(user);

            log.info("Saved user: {}", res);

            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (RegistrationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam String jwt) {
        log.info("Validating your jwt ({})", jwt);
        return jwtService.getJwtValidationResponse(jwt);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {

        /*
        1. Find user in DB
        2. If no user found - return error, ask to register or double-check login and password
        3. If user is found - verify password
        4. If password is incorrect - same as step 2
        5. If password is correct - generate token
         */
        boolean canLogin = userService.doesUserExistAndPasswordIsCorrect(user, false);
        log.info("User {} tries to login. Can they? {}", user, canLogin);

        if (!canLogin) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        try {
            String jwt = jwtService.generateJwtOnLogin(user);

            /**
             * For now, it works, but in the future to perform logout,
             * storing JWTs or their UUIDs will be mandatory.
             * It will be needed for invalidating or blacklisting tokens.
             * 2nd option is more likely to be implemented.
             */

            return new ResponseEntity<>(jwt, HttpStatus.CREATED);
        } catch (LoginException e) {
            log.info("Failed login for username '{}', cause: {}", user.getUsername(), e.getMessage());

            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<Object> getUserDetails(@RequestParam String jwt) {
        return requestService.getUserDetailsResponse(jwt);
    }

}
