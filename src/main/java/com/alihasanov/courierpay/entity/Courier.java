package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "couriers")
public class Courier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @Column(name = "phone_number")
    private String phoneNumber;
    private boolean active;
    @Column(name = "created_at")
    private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); active = true; }
}
