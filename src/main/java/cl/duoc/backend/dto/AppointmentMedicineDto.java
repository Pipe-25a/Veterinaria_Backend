package cl.duoc.backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentMedicineDto {
    private String idAppointmentMedicine;
    private String appointmentId;
    private String medicineId;

    @Min(1)
    private int quantity;

    private String instructions;
}
