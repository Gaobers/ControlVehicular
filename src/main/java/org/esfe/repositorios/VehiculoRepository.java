package org.esfe.repositorios;

import org.esfe.modelos.vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<vehiculo, Long> {

    List<vehiculo> findByClienteId(Long clienteId);
}