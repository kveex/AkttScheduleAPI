package me.kveex.akttapispringed.domain.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEvent {
    UUID id;
    Instant timestamp;

    public static ScheduleEvent create() {
        return new ScheduleEvent(UUID.randomUUID(), Instant.now());
    }
}
