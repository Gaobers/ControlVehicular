package org.esfe.servicio.interfaces;

import org.esfe.dtos.gasto.GastoGuardarDTO;
import org.esfe.dtos.gasto.GastoModificarDTO;
import org.esfe.dtos.gasto.GastoSalidaDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGastoService {

    Page<GastoSalidaDTO> obtenerTodosPaginados(
            Pageable pageable
    );

    List<GastoSalidaDTO> obtenerTodos();

    GastoSalidaDTO obtenerPorId(
            Long id
    );

    GastoSalidaDTO crear(
            GastoGuardarDTO gastoGuardar
    );

    GastoSalidaDTO editar(
            GastoModificarDTO gastoModificar
    );

    void eliminarPorId(
            Long id
    );
}