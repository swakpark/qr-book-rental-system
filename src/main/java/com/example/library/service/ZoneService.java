package com.example.library.service;

import com.example.library.model.Zone;
import com.example.library.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public Zone assignZone(String code) {

        return zoneRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("Zone not found: " + code));
    }
}
