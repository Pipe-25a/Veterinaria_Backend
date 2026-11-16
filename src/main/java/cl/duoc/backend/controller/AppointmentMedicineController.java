package cl.duoc.backend.controller;

import cl.duoc.backend.dto.AppointmentMedicineDto;
import cl.duoc.backend.entity.AppointmentMedicine;
import cl.duoc.backend.repository.AppointmentMedicineRepository;
import cl.duoc.backend.repository.AppointmentRepository;
import cl.duoc.backend.repository.MedicineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointment-medicines")
@RequiredArgsConstructor
public class AppointmentMedicineController extends AbstractCrudController<AppointmentMedicine, AppointmentMedicineDto> {
    private final AppointmentMedicineRepository appointmentMedicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicineRepository medicineRepository;

    @Override
    protected JpaRepository<AppointmentMedicine, String> repository() {
        return appointmentMedicineRepository;
    }

    @Override
    protected AppointmentMedicineDto toDto(AppointmentMedicine entity) {
        AppointmentMedicineDto dto = new AppointmentMedicineDto();
        dto.setIdAppointmentMedicine(entity.getIdAppointmentMedicine());
        dto.setAppointmentId(entity.getAppointment() == null ? null : entity.getAppointment().getIdAppointment());
        dto.setMedicineId(entity.getMedicine() == null ? null : entity.getMedicine().getIdMedicine());
        dto.setQuantity(entity.getQuantity());
        dto.setInstructions(entity.getInstructions());
        return dto;
    }

    @Override
    protected AppointmentMedicine toEntity(AppointmentMedicineDto dto) {
        AppointmentMedicine entity = new AppointmentMedicine();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(AppointmentMedicine entity, AppointmentMedicineDto dto) {
        entity.setAppointment(dto.getAppointmentId() == null ? null : appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada")));
        entity.setMedicine(dto.getMedicineId() == null ? null : medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento no encontrado")));
        entity.setQuantity(dto.getQuantity());
        entity.setInstructions(dto.getInstructions());
    }

    @Override
    protected String id(AppointmentMedicine entity) {
        return entity.getIdAppointmentMedicine();
    }
}
