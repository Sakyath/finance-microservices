package com.zorvyn.user.service;

import com.zorvyn.common.exception.BadRequestException;
import com.zorvyn.common.exception.ResourceNotFoundException;
import com.zorvyn.common.model.enums.Role;
import com.zorvyn.user.dto.CreateUserRequest;
import com.zorvyn.user.dto.UpdateUserRequest;
import com.zorvyn.user.dto.UserResponse;
import com.zorvyn.user.entity.AppUser;
import com.zorvyn.user.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setActive(true);
        return UserResponse.from(repository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return repository.findByRole(role).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return UserResponse.from(findById(id));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        AppUser user = findById(id);
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getActive() != null) user.setActive(request.getActive());
        return UserResponse.from(repository.save(user));
    }

    public void deactivateUser(Long id) {
        AppUser user = findById(id);
        user.setActive(false);
        repository.save(user);
    }

    private AppUser findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
