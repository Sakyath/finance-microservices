package com.zorvyn.user.dto;

import com.zorvyn.common.model.enums.Role;
import jakarta.validation.constraints.*;

public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
}
