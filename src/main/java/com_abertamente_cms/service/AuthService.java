package com_abertamente_cms.service;

import com_abertamente_cms.domain.RefreshToken;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;
import com_abertamente_cms.dto.auth.AuthResponse;
import com_abertamente_cms.dto.auth.LoginRequest;
import com_abertamente_cms.dto.auth.RegisterRequest;
import com_abertamente_cms.dto.auth.TokenRefreshRequest;
import com_abertamente_cms.dto.auth.TokenRefreshResponse;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com_abertamente_cms.dto.user.UserDto;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        User user = new User(request.firstName(), request.lastName(), request.email(), passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        return authenticateAndGenerateTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return authenticateAndGenerateTokens(user);
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não está no banco de dados!"));
    }

    public UserDto getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        
        String role = user.getRole() != null ? user.getRole().name() : "USER";
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), role);
    }

    private AuthResponse authenticateAndGenerateTokens(User user) {
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        String role = user.getRole() != null ? user.getRole().name() : "USER";

        return new AuthResponse(
                jwtToken,
                refreshToken.getToken(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                role
        );
    }
}
