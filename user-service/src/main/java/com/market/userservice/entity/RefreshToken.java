package com.market.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant expiryDate;
}
