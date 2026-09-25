package org.esfe.servicio.interfaces;

import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoGuardar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoModificar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRecordatorioMantenimientoService {

    List<RecordatorioMantenimientoSalida> obtenerTodos();

    Page<RecordatorioMantenimientoSalida> obtenerTodosPaginados(Pageable pageable);

    RecordatorioMantenimientoSalida obtenerPorId(Long id);

    RecordatorioMantenimientoSalida crear(
            RecordatorioMantenimientoGuardar recordatorioMantenimientoGuardar
    );

    RecordatorioMantenimientoSalida editar(
            RecordatorioMantenimientoModificar recordatorioMantenimientoModificar
    );

    void eliminarPorId(Long id);
}