package com.opticoncours.features.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthRecords {

    public record LoginRequest(
            @NotBlank(message = "L'identifiant email est obligatoire")
            @Email(message = "Format d'email invalide")
            String email,

            @NotBlank(message = "Le mot de passe est obligatoire")
            String password
    ) {}

    public record RegisterRequest(
            @NotBlank(message = "L'adresse email est requise")
            @Email(message = "Format d'email invalide")
            String email,

            @NotBlank(message = "Le mot de passe est requis")
            @Size(min = 6, message = "Le mot de passe doit comporter au moins 6 caractères")
            String password,

            @NotBlank(message = "Le nom de famille est requis")
            String nom,

            @NotBlank(message = "Le prénom est requis")
            String prenom,

            String telephone
    ) {}

    public record UserResponse(
            UUID id,
            String email,
            String nom,
            String prenom,
            String telephone,
            Role role,
            LocalDateTime createdAt
    ) {
        public static UserResponse fromUser(User user) {
            if (user == null) return null;
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getTelephone(),
                    user.getRole(),
                    user.getCreatedAt()
            );
        }
    }

    public record AuthResponse(
            String token,
            String tokenType,
            long expiresIn,
            UserResponse user
    ) {
        public static AuthResponse of(String token, long expiresIn, User user) {
            return new AuthResponse(token, "Bearer", expiresIn, UserResponse.fromUser(user));
        }
    }
}
