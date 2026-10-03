package org.esfe.servicio.interfaces;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;

import org.esfe.enums.EstadoMantenimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IMantenimientoService {


    Page<MantenimientoSalidaDTO>
    obtenerTodosPaginados(

            Long vehiculoId,

            EstadoMantenimiento estado,

            Boolean activo,

            Pageable pageable
    );


    List<MantenimientoSalidaDTO>
    obtenerTodos(

            Long vehiculoId,

            EstadoMantenimiento estado,

            Boolean activo
    );


    MantenimientoSalidaDTO obtenerPorId(
            Long id
    );


    MantenimientoSalidaDTO crear(
            MantenimientoGuardarDTO mantenimientoGuardar
    );


    MantenimientoSalidaDTO editar(
            MantenimientoModificarDTO mantenimientoModificar
    );


    void eliminarPorId(
            Long id
    );
}