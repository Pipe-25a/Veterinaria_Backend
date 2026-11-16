package cl.duoc.backend.controller;

import cl.duoc.backend.dto.PetDto;
import cl.duoc.backend.entity.Pet;
import cl.duoc.backend.repository.PetRepository;
import cl.duoc.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController extends AbstractCrudController<Pet, PetDto> {
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    protected JpaRepository<Pet, String> repository() {
        return petRepository;
    }

    @Override
    protected PetDto toDto(Pet entity) {
        PetDto dto = new PetDto();
        dto.setIdPet(entity.getIdPet());
        dto.setName(entity.getName());
        dto.setOwnerId(entity.getOwner() == null ? null : entity.getOwner().getIdUser());
        return dto;
    }

    @Override
    protected Pet toEntity(PetDto dto) {
        Pet entity = new Pet();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(Pet entity, PetDto dto) {
        entity.setName(dto.getName());
        entity.setOwner(dto.getOwnerId() == null ? null : userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dueño no encontrado")));
    }

    @Override
    protected String id(Pet entity) {
        return entity.getIdPet();
    }
}
