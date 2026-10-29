package me.kveex.akttapispringed.domain.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kveex.akttapispringed.domain.entity.schedule.Subgroup;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {
    @NotBlank
    private String value;
    @NotNull
    private ScheduleTarget target;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate scheduleDate;
    private Subgroup subgroup = Subgroup.BOTH;
}
