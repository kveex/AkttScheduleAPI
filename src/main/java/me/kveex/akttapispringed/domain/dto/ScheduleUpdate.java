package me.kveex.akttapispringed.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ScheduleUpdate {
    UUID id;
    Instant timestamp;

    public static ScheduleUpdate create() {
        return new ScheduleUpdate(UUID.randomUUID(), Instant.now());
    }
}
