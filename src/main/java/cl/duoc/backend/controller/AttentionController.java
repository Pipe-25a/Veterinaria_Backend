package cl.duoc.backend.controller;

import cl.duoc.backend.dto.AttentionDto;
import cl.duoc.backend.entity.Attention;
import cl.duoc.backend.repository.AppointmentRepository;
import cl.duoc.backend.repository.AttentionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attentions")
@RequiredArgsConstructor
public class AttentionController extends AbstractCrudController<Attention, AttentionDto> {
    private final AttentionRepository attentionRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    protected JpaRepository<Attention, String> repository() {
        return attentionRepository;
    }

    @Override
    protected AttentionDto toDto(Attention entity) {
        AttentionDto dto = new AttentionDto();
        dto.setIdAttention(entity.getIdAttention());
        dto.setAppointmentId(entity.getAppointment() == null ? null : entity.getAppointment().getIdAppointment());
        dto.setDiagnosis(entity.getDiagnosis());
        dto.setTreatment(entity.getTreatment());
        dto.setObservations(entity.getObservations());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Override
    protected Attention toEntity(AttentionDto dto) {
        Attention entity = new Attention();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(Attention entity, AttentionDto dto) {
        entity.setAppointment(dto.getAppointmentId() == null ? null : appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada")));
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setTreatment(dto.getTreatment());
        entity.setObservations(dto.getObservations());
    }

    @Override
    protected String id(Attention entity) {
        return entity.getIdAttention();
    }
}
