package com.jobready.users;

public record UserDto(String id, String name, String email, String role) {

    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
