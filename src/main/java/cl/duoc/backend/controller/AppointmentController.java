package cl.duoc.backend.controller;

import cl.duoc.backend.dto.AppointmentDto;
import cl.duoc.backend.entity.Appointment;
import cl.duoc.backend.entity.Pet;
import cl.duoc.backend.entity.User;
import cl.duoc.backend.entity.VeterinaryService;
import cl.duoc.backend.repository.AppointmentRepository;
import cl.duoc.backend.repository.PetRepository;
import cl.duoc.backend.repository.UserRepository;
import cl.duoc.backend.repository.VeterinaryServiceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController extends AbstractCrudController<Appointment, AppointmentDto> {
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final VeterinaryServiceRepository serviceRepository;

    @Override
    protected JpaRepository<Appointment, String> repository() {
        return appointmentRepository;
    }

    @Override
    protected AppointmentDto toDto(Appointment entity) {
        AppointmentDto dto = new AppointmentDto();
        dto.setIdAppointment(entity.getIdAppointment());
        dto.setPetId(entity.getPet() == null ? null : entity.getPet().getIdPet());
        dto.setUserId(entity.getUser() == null ? null : entity.getUser().getIdUser());
        dto.setServiceId(entity.getService() == null ? null : entity.getService().getIdService());
        dto.setScheduledAt(entity.getScheduledAt());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    @Override
    protected Appointment toEntity(AppointmentDto dto) {
        Appointment entity = new Appointment();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(Appointment entity, AppointmentDto dto) {
        entity.setPet(findPet(dto.getPetId()));
        entity.setUser(findUser(dto.getUserId()));
        entity.setService(findService(dto.getServiceId()));
        entity.setScheduledAt(dto.getScheduledAt());
        entity.setStatus(dto.getStatus());
    }

    private Pet findPet(String petId) {
        if (petId == null) {
            return null;
        }
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
    }

    private User findUser(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private VeterinaryService findService(String serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    @Override
    protected String id(Appointment entity) {
        return entity.getIdAppointment();
    }
}
