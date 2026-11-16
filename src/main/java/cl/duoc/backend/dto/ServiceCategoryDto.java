package cl.duoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceCategoryDto {
    private String idServiceCategory;

    @NotBlank
    private String name;
}
