package com.efforttracker.service;

import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() { return userRepository.findAll(); }

    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User create(UserDtos.CreateUserRequest req) {
        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword());
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        String role = req.getRole() != null ? req.getRole().toUpperCase() : "USER";
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            role = "USER";
        }
        u.setRole(role);
        if (req.getHourlyRate() != null) {
            try {
                u.setHourlyRate(new BigDecimal(req.getHourlyRate()));
            } catch (NumberFormatException ex) {
                u.setHourlyRate(BigDecimal.ZERO);
            }
        } else {
            u.setHourlyRate(BigDecimal.ZERO);
        }
        u.setNotes(req.getNotes());
        return userRepository.save(u);
    }

    public User update(String id, UserDtos.UpdateUserRequest req) {
        User u = getById(id);
        if (req.getFirstName() != null) u.setFirstName(req.getFirstName());
        if (req.getLastName() != null) u.setLastName(req.getLastName());
        if (req.getRole() != null) u.setRole(req.getRole());
        if (req.getHourlyRate() != null) {
            try {
                u.setHourlyRate(new BigDecimal(req.getHourlyRate()));
            } catch (NumberFormatException ex) {

            }
        }
        if (req.getNotes() != null) u.setNotes(req.getNotes());
        return userRepository.save(u);
    }

    public void delete(String id) { userRepository.deleteById(id); }
}