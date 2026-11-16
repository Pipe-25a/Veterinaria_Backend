package cl.duoc.backend.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttentionDto {
    private String idAttention;
    private String appointmentId;
    private String diagnosis;
    private String treatment;
    private String observations;
    private LocalDateTime createdAt;
}
