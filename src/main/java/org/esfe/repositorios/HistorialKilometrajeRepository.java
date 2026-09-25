package org.esfe.repositorios;

import org.esfe.modelos.HistorialKilometraje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialKilometrajeRepository extends JpaRepository<HistorialKilometraje, Long> {
    List<HistorialKilometraje> findByVehiculoIdOrderByFechaRegistroDesc(Long vehiculoId);

    Optional<HistorialKilometraje> findTopByVehiculoIdOrderByKilometrajeDesc(Long vehiculoId);
}