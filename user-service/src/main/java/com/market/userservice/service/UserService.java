package com.market.userservice.service;

import com.market.userservice.entity.User;
import com.market.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public User loadUserById(Long userId) {
        return repo.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
    public String register(String username, String rawPassword) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(List.of("INVESTOR"));

        repo.save(user);
        log.info("Saved user: {}", username);

        return "User registered successfully"; // ✅ return success message
    }

}
