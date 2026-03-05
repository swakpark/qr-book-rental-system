package com.example.library.repository;

import com.example.library.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCode(String code);

    Optional<Zone> findByCodeAndFloor(String code, int floor);

    Optional<Zone> findByName(String name);

    List<Zone> findByFloor(int floor);
}
