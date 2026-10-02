package org.esfe.repositorios;

import org.esfe.enums.EstadoMantenimiento;
import org.esfe.modelos.Mantenimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MantenimientoRepository
        extends JpaRepository<Mantenimiento, Long> {


    @Query("""
            SELECT m
            FROM Mantenimiento m
            WHERE (:vehiculoId IS NULL OR m.vehiculoId = :vehiculoId)
              AND (:estado IS NULL OR m.estado = :estado)
              AND (:activo IS NULL OR m.activo = :activo)
            ORDER BY m.id DESC
            """)
    List<Mantenimiento> buscarPorFiltros(

            @Param("vehiculoId")
            Long vehiculoId,

            @Param("estado")
            EstadoMantenimiento estado,

            @Param("activo")
            Boolean activo
    );


    @Query("""
            SELECT m
            FROM Mantenimiento m
            WHERE (:vehiculoId IS NULL OR m.vehiculoId = :vehiculoId)
              AND (:estado IS NULL OR m.estado = :estado)
              AND (:activo IS NULL OR m.activo = :activo)
            ORDER BY m.id DESC
            """)
    Page<Mantenimiento> buscarPorFiltrosPaginados(

            @Param("vehiculoId")
            Long vehiculoId,

            @Param("estado")
            EstadoMantenimiento estado,

            @Param("activo")
            Boolean activo,

            Pageable pageable
    );
}