package cl.duoc.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attentions")
@Getter
@Setter
@NoArgsConstructor
public class Attention {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idAttention;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String diagnosis;

    private String treatment;

    private String observations;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
