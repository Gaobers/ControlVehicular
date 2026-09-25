package org.esfe.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.esfe.modelos.vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<vehiculo, Long> {
    boolean existsByPlaca(String placa);
}
