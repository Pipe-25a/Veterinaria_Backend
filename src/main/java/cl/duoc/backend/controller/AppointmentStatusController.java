package cl.duoc.backend.controller;

import cl.duoc.backend.dto.AppointmentStatusDto;
import cl.duoc.backend.entity.AppointmentStatus;
import cl.duoc.backend.repository.AppointmentStatusRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointment-statuses")
@RequiredArgsConstructor
public class AppointmentStatusController extends AbstractCrudController<AppointmentStatus, AppointmentStatusDto> {
    private final AppointmentStatusRepository statusRepository;

    @Override
    protected JpaRepository<AppointmentStatus, String> repository() {
        return statusRepository;
    }

    @Override
    protected AppointmentStatusDto toDto(AppointmentStatus entity) {
        AppointmentStatusDto dto = new AppointmentStatusDto();
        dto.setIdAppointmentStatus(entity.getIdAppointmentStatus());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    protected AppointmentStatus toEntity(AppointmentStatusDto dto) {
        AppointmentStatus entity = new AppointmentStatus();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(AppointmentStatus entity, AppointmentStatusDto dto) {
        entity.setName(dto.getName());
    }

    @Override
    protected String id(AppointmentStatus entity) {
        return entity.getIdAppointmentStatus();
    }
}