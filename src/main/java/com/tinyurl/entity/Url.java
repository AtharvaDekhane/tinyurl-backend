package com.tinyurl.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String shortCode;

    @Column(columnDefinition = "TEXT")
    private String originalUrl;

    private LocalDateTime createdAt;

    private Long clickCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}