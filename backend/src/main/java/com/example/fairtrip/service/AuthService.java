package com.example.fairtrip.service;

import com.example.fairtrip.dto.LoginRequest;
import com.example.fairtrip.dto.LoginResponse;
import com.example.fairtrip.dto.RegisterRequest;
import com.example.fairtrip.entity.Role;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.RoleName;
import com.example.fairtrip.exception.ResourceNotFoundException;
import com.example.fairtrip.repository.RoleRepository;
import com.example.fairtrip.repository.UserRepository;
import com.example.fairtrip.repository.VendorRepository;
import com.example.fairtrip.security.CustomUserDetails;
import com.example.fairtrip.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       VendorRepository vendorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.vendorRepository = vendorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String roleName = user.getRole().getName().name();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails, roleName);

        Long vendorId = null;
        String vendorCode = null;

        if (user.getRole().getName() == RoleName.VENDOR) {
            Optional<Vendor> vendorOpt = vendorRepository.findByUserId(user.getId());
            if (vendorOpt.isPresent()) {
                vendorId = vendorOpt.get().getId();
                vendorCode = vendorOpt.get().getVendorCode();
            }
        }

        log.info("User {} successfully logged in with role {}", user.getEmail(), roleName);
        return new LoginResponse(token, roleName, user.getId(), user.getName(), user.getEmail(), vendorId, vendorCode);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        RoleName roleName = request.getRole() != null ? request.getRole() : RoleName.NORMAL_USER;
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        User savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails, roleName.name());

        log.info("New user registered: {} ({})", savedUser.getEmail(), roleName);
        return new LoginResponse(token, roleName.name(), savedUser.getId(), savedUser.getName(), savedUser.getEmail(), null, null);
    }
}
