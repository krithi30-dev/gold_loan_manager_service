package com.goldloan.service;
import com.goldloan.dto.user.*;
import com.goldloan.entity.*;
import com.goldloan.exception.*;
import com.goldloan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    public UserResponse create(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("DUPLICATE_EMAIL", "User with this email already exists");
        Branch branch = req.getBranchId() != null
                ? branchRepository.findById(UUID.fromString(req.getBranchId())).orElse(null) : null;
        UserEntity u = UserEntity.builder().name(req.getName()).email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.valueOf(req.getRole())).branch(branch).active(true).build();
        return toResponse(userRepository.save(u));
    }
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString())));
    }
    public UserResponse toggleActive(UUID id) {
        UserEntity u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        u.setActive(!u.isActive());
        return toResponse(userRepository.save(u));
    }
    private UserResponse toResponse(UserEntity u) {
        return UserResponse.builder().id(u.getId()).name(u.getName()).email(u.getEmail())
                .role(u.getRole().name())
                .branchId(u.getBranch() != null ? u.getBranch().getId().toString() : null)
                .branchName(u.getBranch() != null ? u.getBranch().getName() : null)
                .active(u.isActive()).createdAt(u.getCreatedAt()).build();
    }
}
