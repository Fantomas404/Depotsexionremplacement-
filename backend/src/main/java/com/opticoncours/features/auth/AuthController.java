package com.opticoncours.features.auth;

import com.opticoncours.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification & Sécurité", description = "Endpoints d'inscription, de connexion et d'obtention du profil courant")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouveau compte candidat")
    public ResponseEntity<ApiResponse<AuthRecords.AuthResponse>> register(@Valid @RequestBody AuthRecords.RegisterRequest request) {
        AuthRecords.AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compte candidat créé avec succès.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion candidat ou administrateur et obtention du token JWT")
    public ResponseEntity<ApiResponse<AuthRecords.AuthResponse>> login(@Valid @RequestBody AuthRecords.LoginRequest request) {
        AuthRecords.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Connexion établie avec succès.", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Récupération du profil de l'utilisateur actuellement connecté")
    public ResponseEntity<ApiResponse<AuthRecords.UserResponse>> getCurrentUser() {
        User user = authService.getAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(AuthRecords.UserResponse.fromUser(user)));
    }
}
