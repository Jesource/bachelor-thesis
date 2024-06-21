package vu.jesource.authentication.web.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vu.jesource.authentication.web.exceptions.LoginException;
import vu.jesource.authentication.web.exceptions.RegistrationException;
import vu.jesource.authentication.web.models.User;
import vu.jesource.authentication.web.repos.UserRepository;

import java.security.MessageDigest;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Check if the username or email already exists
        if (isEmailOrUsernameAlreadyTaken(user)) {
            throw new RegistrationException("Username or email already in use");
        }

        // Encode the password before saving it to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    private boolean isEmailOrUsernameAlreadyTaken(User user) {
        return userRepository.findByUsername(user.getUsername()).isPresent() ||
                userRepository.findByEmail(user.getEmail()).isPresent();
    }


    public boolean doesUserExistAndPasswordIsCorrect(User user, boolean isPasswordHashed) {
        log.info("Analysing {}", user);
        log.debug("Analysing {}", user);
        boolean isPasswordCorrect = false;
        boolean userExist = doesUserExist(user);

        if (userExist) {
            if (isPasswordHashed) {
                isPasswordCorrect = isUserHashedPasswordCorrect(user);
            }
            else {
                isPasswordCorrect = isUserPasswordCorrect(user);
            }
            log.debug("User '{}' exists. Is password correct: {}.", user.getUsername(), isPasswordCorrect);
        }

        return userExist && isPasswordCorrect;
    }

    private boolean isUserHashedPasswordCorrect(User user) {
        // User existence was checked before
        User dbUser = userRepository.findByUsername(user.getUsername()).get();

        boolean passwordMatch = MessageDigest.isEqual(dbUser.getPassword().getBytes(), user.getPassword().getBytes());
        log.debug("Are provided and stored passwords for user {} matching? {}", dbUser.getUserId(), passwordMatch);

        return passwordMatch;
    }

    private boolean isUserPasswordCorrect(User user) {
        // User existence was checked before
        User dbUser = userRepository.findByUsername(user.getUsername()).get();

//        boolean passwordMatch = MessageDigest.isEqual(dbUser.getPassword().getBytes(), user.getPassword().getBytes());
//        log.debug("Are provided and stored passwords for user {} matching? {}", dbUser.getUserId(), passwordMatch);
        return passwordEncoder.matches(user.getPassword(), dbUser.getPassword());
//        return passwordMatch;
    }

    private boolean doesUserExist(User user) {
        Optional<User> dbUser = userRepository.findByUsername(user.getUsername());

        if (dbUser.isEmpty()) {
            log.info("User with username '{}' was not found", user.getUsername());
            return false;
        }

        return true;
    }

    public User getUserWhoTriesToLogin(User user) throws LoginException {
        if (!doesUserExist(user)) {
            throw new LoginException(String.format("User with username '%s' is not registered", user.getUsername()));
        }

        return userRepository.findByUsername(user.getUsername()).get();
    }
}

