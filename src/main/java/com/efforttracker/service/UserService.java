package com.efforttracker.service;

import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.UserRepository;
import com.efforttracker.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + id));
    }

    public User create(UserDtos.CreateUserRequest req) {
        User u = new User();
        u.setEmail(req.getEmail());

        // mã hoá password trước khi lưu
        u.setPassword(passwordEncoder.encode(req.getPassword()));

        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());

        // chuẩn hoá role
        String role = req.getRole() != null ? req.getRole().toUpperCase() : "USER";
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            role = "USER";
        }
        u.setRole(role);

        // set hourlyRate
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

        if (req.getRole() != null) {
            String role = req.getRole().toUpperCase();
            if (role.equals("USER") || role.equals("ADMIN")) {
                u.setRole(role);
            }
        }

        if (req.getHourlyRate() != null) {
            try {
                u.setHourlyRate(new BigDecimal(req.getHourlyRate()));
            } catch (NumberFormatException ex) {
                u.setHourlyRate(BigDecimal.ZERO);
            }
        }

        if (req.getNotes() != null) u.setNotes(req.getNotes());
        return userRepository.save(u);
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy User với id = " + id);
        }
        userRepository.deleteById(id);
    }
    public UserDtos.UserResponse toResponse(User u) {
        if (u == null) return null;
        return new UserDtos.UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole(),
                u.getHourlyRate(),
                u.getNotes(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
