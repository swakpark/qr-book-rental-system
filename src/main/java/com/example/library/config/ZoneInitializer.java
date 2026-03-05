package com.example.library.config;

import com.example.library.model.Zone;
import com.example.library.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZoneInitializer implements ApplicationRunner {

    private final ZoneRepository zoneRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (zoneRepository.count() > 0) return;

        // 1층
        zoneRepository.save(new Zone(1, "000", "총류", 5, 5, 25, 25));
        zoneRepository.save(new Zone(1, "100", "철학", 35, 5, 25, 25));
        zoneRepository.save(new Zone(1, "200", "종교", 65, 5, 25, 25));
        zoneRepository.save(new Zone(1, "300", "사회과학", 5, 40, 40, 30));
        zoneRepository.save(new Zone(1, "400", "자연과학", 50, 40, 40, 30));

        // 2층
        zoneRepository.save(new Zone(2, "500", "기술과학", 6, 6, 25, 25));
        zoneRepository.save(new Zone(2, "600", "예술", 26, 6, 25, 25));
        zoneRepository.save(new Zone(2, "700", "언어", 46, 6, 25, 25));
        zoneRepository.save(new Zone(2, "800", "문학", 6, 32, 40, 30));
        zoneRepository.save(new Zone(2, "900", "역사", 36, 32, 40, 30));
    }
}
