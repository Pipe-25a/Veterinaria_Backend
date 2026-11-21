package cl.duoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VeterinaryServiceDto {
    private String idService;

    @NotBlank
    private String name;

    private String description;
    private String categoryId;
}
