package cl.duoc.backend.controller;

import cl.duoc.backend.dto.ReportDto;
import cl.duoc.backend.entity.Report;
import cl.duoc.backend.repository.ReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController extends AbstractCrudController<Report, ReportDto> {
    private final ReportRepository reportRepository;

    @Override
    protected JpaRepository<Report, String> repository() {
        return reportRepository;
    }

    @Override
    protected ReportDto toDto(Report entity) {
        ReportDto dto = new ReportDto();
        dto.setIdReport(entity.getIdReport());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    protected Report toEntity(ReportDto dto) {
        Report entity = new Report();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(Report entity, ReportDto dto) {
        entity.setName(dto.getName());
    }

    @Override
    protected String id(Report entity) {
        return entity.getIdReport();
    }
}
