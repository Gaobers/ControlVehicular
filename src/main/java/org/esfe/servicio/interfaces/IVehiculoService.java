package org.esfe.servicio.interfaces;

import org.esfe.dtos.Vehiculo.VehiculoGuardarDTO;
import org.esfe.dtos.Vehiculo.VehiculoModificarDTO;
import org.esfe.dtos.Vehiculo.VehiculoSalidaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVehiculoService {

    Page<VehiculoSalidaDTO> obtenerTodosPaginados(
            Pageable pageable
    );

    List<VehiculoSalidaDTO> obtenerTodos();

    VehiculoSalidaDTO obtenerPorId(
            Long id
    );

    VehiculoSalidaDTO crear(
            VehiculoGuardarDTO vehiculoGuardar
    );

    VehiculoSalidaDTO editar(
            VehiculoModificarDTO vehiculoModificar
    );

    void eliminarPorId(
            Long id
    );
}