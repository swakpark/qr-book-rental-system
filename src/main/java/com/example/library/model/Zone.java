package com.example.library.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "zone")
    private List<Shelf> shelves = new ArrayList<>();

    @Column(nullable = false)
    private int floor; // 1층, 2층

    @Column(nullable = false)
    private String code; // 000, 100, 200 등

    @Column(nullable = false)
    private String name; // 총류, 철학 등

    // SVG 영역 기준 좌표 (%)
    @Column(nullable = false)
    private Integer offsetX; // 왼쪽 시작 %

    @Column(nullable = false)
    private Integer offsetY; // 위쪽 시작 %

    @Column(nullable = false)
    private int width; // 영역 너비 %

    @Column(nullable = false)
    private int height; // 영역 높이 %

    public Zone(int floor, String code, String name,
                Integer offsetX, Integer offsetY, int width, int height) {
        this.floor = floor;
        this.code = code;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }
}