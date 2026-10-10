package com.opticoncours.features.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_comptes_utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "utilisateur_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Email
    @Column(name = "adresse_email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @JsonIgnore
    @Column(name = "mot_de_passe_hash", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "nom_famille", nullable = false)
    private String nom;

    @NotBlank
    @Column(name = "prenoms", nullable = false)
    private String prenom;

    @Column(name = "numero_telephone", unique = true)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(name = "profil_role", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "date_inscription", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
