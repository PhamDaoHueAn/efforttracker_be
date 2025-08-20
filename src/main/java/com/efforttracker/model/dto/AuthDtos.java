package com.efforttracker.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
  public static class LoginRequest {
    @Email
    private String email;
    @NotBlank
    private String password;

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserDtos.UserResponse me;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String tokenType, long expiresIn, UserDtos.UserResponse me) {
      this.accessToken = accessToken;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
      this.me = me;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
    }

    public String getTokenType() {
      return tokenType;
    }

    public void setTokenType(String tokenType) {
      this.tokenType = tokenType;
    }

    public long getExpiresIn() {
      return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
      this.expiresIn = expiresIn;
    }

    public UserDtos.UserResponse getMe() {
      return me;
    }

    public void setMe(UserDtos.UserResponse me) {
      this.me = me;
    }
  }

  public static class RegisterRequest {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    @NotBlank
    private String role;

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }
}