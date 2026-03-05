package com.example.library.config;

import com.example.library.model.Shelf;
import com.example.library.model.Zone;
import com.example.library.repository.ZoneRepository;
import com.example.library.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShelfInitializer implements ApplicationRunner {

    private final ZoneRepository zoneRepository;
    private final ShelfRepository shelfRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (shelfRepository.count() > 0) return; // 이미 생성됐으면 패스

        createShelves("000", "A", 1);
        createShelves("100", "B", 2);
        createShelves("200", "C", 1);
        createShelves("300", "D", 5);
        createShelves("400", "E", 3);
        createShelves("500", "F", 3);
        createShelves("600", "G", 2);
        createShelves("700", "H", 1);
        createShelves("800", "I", 9);
        createShelves("900", "J", 3);
    }

    private void createShelves(String zoneCode, String prefix, int count) {

        Zone zone = zoneRepository.findByCode(zoneCode)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneCode));

        for (int i = 1; i <= count; i++) {
            Shelf shelf = Shelf.builder()
                    .zone(zone)
                    .code(prefix + "-" + i)
                    .levels(4)
                    .build();

            shelfRepository.save(shelf);
        }
    }
}
