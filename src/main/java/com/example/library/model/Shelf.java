package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 Zone에 속하는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    // 예: A-1, D-3
    @Column(nullable = false)
    private String code;

    // 기본 4단
    @Column(nullable = false)
    private int levels = 4;
}
