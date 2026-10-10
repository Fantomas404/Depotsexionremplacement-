package com.opticoncours.features.auth;

import com.opticoncours.common.exception.BadRequestException;
import com.opticoncours.common.exception.ConflictException;
import com.opticoncours.common.exception.ResourceNotFoundException;
import com.opticoncours.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthRecords.AuthResponse register(AuthRecords.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Cette adresse email est déjà associée à un compte existant.");
        }

        if (req.telephone() != null && !req.telephone().isBlank() && userRepository.existsByTelephone(req.telephone())) {
            throw new ConflictException("Ce numéro de téléphone est déjà utilisé par un autre compte.");
        }

        User user = User.builder()
                .email(req.email().trim().toLowerCase())
                .password(passwordEncoder.encode(req.password()))
                .nom(req.nom().trim().toUpperCase())
                .prenom(req.prenom().trim())
                .telephone(req.telephone() != null ? req.telephone().trim() : null)
                .role(Role.ROLE_CANDIDAT)
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthRecords.AuthResponse.of(token, jwtService.getExpirationMs(), user);
    }

    public AuthRecords.AuthResponse login(AuthRecords.LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(), req.password())
        );

        User user = userRepository.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Identifiants de connexion invalides."));

        String token = jwtService.generateToken(user);
        return AuthRecords.AuthResponse.of(token, jwtService.getExpirationMs(), user);
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("Aucune session utilisateur active.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Compte utilisateur introuvable : " + auth.getName()));
    }
}
