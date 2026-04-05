package com.taskapp.taskservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserDetails {
    private String username;
    private String userId;
    private String role;
}
