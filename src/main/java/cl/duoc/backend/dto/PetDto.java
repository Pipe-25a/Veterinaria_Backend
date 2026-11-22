package cl.duoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PetDto {
    private String idPet;

    @NotBlank
    private String name;

    private String ownerId;

    private String species;
    private String breed; 
    private Integer age; 
}
