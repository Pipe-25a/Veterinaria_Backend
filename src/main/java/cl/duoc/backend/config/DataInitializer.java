package cl.duoc.backend.config;

import cl.duoc.backend.entity.AppointmentStatus;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.repository.AppointmentStatusRepository;
import cl.duoc.backend.repository.RoleRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializa los datos base de la aplicación.
 * Al arrancar contra una BD vacía (como una RDS de AWS recién creada),
 * inserta los roles y estados mínimos que el sistema necesita para funcionar.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    // 👇 AHORA INYECTAMOS TAMBIÉN EL REPOSITORIO DE ESTADOS
    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
            AppointmentStatusRepository statusRepository) {
        return args -> {
            // 1. Crear Roles
            List<String> rolesRequeridos = List.of("ADMIN", "VETERINARIO", "CLIENTE");
            for (String nombre : rolesRequeridos) {
                if (roleRepository.findByName(nombre).isEmpty()) {
                    Role role = new Role();
                    role.setName(nombre);
                    roleRepository.save(role);
                    log.info("Rol creado: {}", nombre);
                }
            }
            log.info("✅ Roles base verificados. Total en BD: {}", roleRepository.count());

            // 2. Crear Estados de Cita
            List<String> estadosRequeridos = List.of("PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA");
            for (String nombre : estadosRequeridos) {
                if (statusRepository.findByName(nombre).isEmpty()) {
                    AppointmentStatus status = new AppointmentStatus();
                    status.setName(nombre);
                    statusRepository.save(status);
                    log.info("Estado de cita creado: {}", nombre);
                }
            }
            log.info("✅ Estados base verificados. Total en BD: {}", statusRepository.count());
        };
    }
}