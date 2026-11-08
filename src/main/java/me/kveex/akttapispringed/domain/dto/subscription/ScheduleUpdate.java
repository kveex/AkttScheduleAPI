package me.kveex.akttapispringed.domain.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUpdate {
    UUID id;
    ScheduleEvent event;
    Instant timestamp;

    public static ScheduleUpdate create(ScheduleEvent event) {
        return new ScheduleUpdate(UUID.randomUUID(), event, Instant.now());
    }
}
