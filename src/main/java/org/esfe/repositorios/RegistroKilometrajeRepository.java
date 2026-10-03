package org.esfe.repositorios;

import org.esfe.modelos.RegistroKilometraje;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RegistroKilometrajeRepository
        extends JpaRepository<RegistroKilometraje, Long> {


    @Query("""
            SELECT r
            FROM RegistroKilometraje r
            WHERE (:vehiculoId IS NULL OR r.vehiculoId = :vehiculoId)
              AND (:activo IS NULL OR r.activo = :activo)
            ORDER BY r.fechaHora DESC, r.id DESC
            """)
    List<RegistroKilometraje> buscarPorFiltros(

            @Param("vehiculoId")
            Long vehiculoId,

            @Param("activo")
            Boolean activo
    );


    @Query(
            value = """
                    SELECT r
                    FROM RegistroKilometraje r
                    WHERE (:vehiculoId IS NULL OR r.vehiculoId = :vehiculoId)
                      AND (:activo IS NULL OR r.activo = :activo)
                    ORDER BY r.fechaHora DESC, r.id DESC
                    """,

            countQuery = """
                    SELECT COUNT(r)
                    FROM RegistroKilometraje r
                    WHERE (:vehiculoId IS NULL OR r.vehiculoId = :vehiculoId)
                      AND (:activo IS NULL OR r.activo = :activo)
                    """
    )
    Page<RegistroKilometraje> buscarPorFiltrosPaginados(

            @Param("vehiculoId")
            Long vehiculoId,

            @Param("activo")
            Boolean activo,

            Pageable pageable
    );


    Optional<RegistroKilometraje>
    findTopByVehiculoIdAndActivoTrueOrderByKilometrajeDescFechaHoraDesc(
            Long vehiculoId
    );
}