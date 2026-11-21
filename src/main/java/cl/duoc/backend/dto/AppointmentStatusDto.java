package cl.duoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentStatusDto {
    private String idAppointmentStatus;

    @NotBlank
    private String name;
}