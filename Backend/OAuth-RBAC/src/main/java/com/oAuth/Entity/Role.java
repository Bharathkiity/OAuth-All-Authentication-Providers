package com.oAuth.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;  // e.g. "ROLE_USER", "ROLE_ADMIN"
}
