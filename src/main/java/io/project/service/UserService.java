package io.project.service;

import io.project.dto.userDto.UserCreateDTO;
import io.project.dto.userDto.UserDTO;
import io.project.dto.userDto.UserUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.mapper.UserMapper;
import io.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
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
