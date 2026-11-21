package cl.duoc.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_categories")
@Getter
@Setter
@NoArgsConstructor
public class ServiceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idServiceCategory;

    @Column(nullable = false, unique = true)
    private String name;
}
