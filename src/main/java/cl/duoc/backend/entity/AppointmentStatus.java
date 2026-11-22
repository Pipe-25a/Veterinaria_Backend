package cl.duoc.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointment_statuses")
@Getter
@Setter
@NoArgsConstructor
public class AppointmentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idAppointmentStatus;

    @Column(nullable = false, unique = true)
    private String name;
}