package io.project.service;

import io.project.dto.userDto.UserCreateDTO;
import io.project.dto.userDto.UserDTO;
import io.project.dto.userDto.UserUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.mapper.UserMapper;
import io.project.model.User;
import io.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String email = jwt.getClaim("sub");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.map(userData);

        var hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPasswordDigest(hashedPassword);

        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.update(userData, user);

        var hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPasswordDigest(hashedPassword);

        userRepository.save(user);

        return userMapper.map(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
