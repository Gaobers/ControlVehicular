package org.esfe.repositorios;

import org.esfe.modelos.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository
        extends JpaRepository<Vehiculo, Long> {

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCaseAndIdNot(
            String placa,
            Long id
    );

    boolean existsByVinIgnoreCase(String vin);

    boolean existsByVinIgnoreCaseAndIdNot(
            String vin,
            Long id
    );
}