package vu.jesource.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .authorizeRequests()
                .requestMatchers(
                        antMatcher(HttpMethod.POST, "/api/auth/register"),
                        antMatcher(HttpMethod.POST, "/api/auth/login"),
                        antMatcher("/api/auth/hi"),
                        antMatcher("/api/auth/validate-token"),
                        antMatcher("/api/auth/get-user-details")
                ).permitAll()
                .anyRequest().authenticated();


        // Disable CSRF protection for a specific endpoint
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                antMatcher(HttpMethod.POST, "/api/auth/register"),
                antMatcher(HttpMethod.POST, "/api/auth/login")
        ));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
