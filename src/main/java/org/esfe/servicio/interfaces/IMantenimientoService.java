package org.esfe.servicio.interfaces;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;
import org.esfe.enums.EstadoMantenimiento;

import java.util.List;
import java.util.Optional;

public interface IMantenimientoService {

    List<MantenimientoSalidaDTO> obtenerTodos(
            Long vehiculoId,
            EstadoMantenimiento estado
    );

    Optional<MantenimientoSalidaDTO> obtenerPorId(Long id);

    MantenimientoSalidaDTO crear(MantenimientoGuardarDTO dto);

    MantenimientoSalidaDTO modificar(MantenimientoModificarDTO dto);

    boolean eliminarPorId(Long id);
}