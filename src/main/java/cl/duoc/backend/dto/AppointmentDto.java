package cl.duoc.backend.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDto {
    private String idAppointment;
    private String petId;
    private String userId;
    private String serviceId;
    private LocalDateTime scheduledAt;
    private String status;
}
