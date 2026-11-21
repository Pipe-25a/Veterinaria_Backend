package cl.duoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicineCategoryDto {
    private String idMedicineCategory;

    @NotBlank
    private String name;
}
