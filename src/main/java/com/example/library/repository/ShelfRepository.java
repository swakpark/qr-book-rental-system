package com.example.library.repository;

import com.example.library.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    @Query(value = """
        SELECT s.id AS shelfId, levels.lvl AS shelfLevel
        FROM shelf s
        CROSS JOIN (
            SELECT 1 AS lvl UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
        ) levels
        LEFT JOIN book b
            ON b.shelf_id = s.id
           AND b.shelf_level = levels.lvl
        WHERE s.zone_id = :zoneId
        GROUP BY s.id, levels.lvl
        HAVING COUNT(b.id) < 25
        ORDER BY s.code ASC, levels.lvl ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<AutoLocationProjection> findFirstAvailableLocation(
            @Param("zoneId") Long zoneId
    );
}
