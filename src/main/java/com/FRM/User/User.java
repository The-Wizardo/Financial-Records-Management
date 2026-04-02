package com.FRM.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long userId;

    @Column(name = "user_name", length = 20)
    private String userName;

    private String password;
    @Column(unique = true)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Roles> roles =new HashSet<>(Set.of(Roles.VIEWER));
}
