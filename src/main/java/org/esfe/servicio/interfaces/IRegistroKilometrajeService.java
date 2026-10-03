package org.esfe.servicio.interfaces;

import org.esfe.dtos.kilometraje.RegistroKilometrajeGuardarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeModificarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeSalidaDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IRegistroKilometrajeService {


    Page<RegistroKilometrajeSalidaDTO>
    obtenerTodosPaginados(

            Long vehiculoId,

            Boolean activo,

            Pageable pageable
    );


    List<RegistroKilometrajeSalidaDTO>
    obtenerTodos(

            Long vehiculoId,

            Boolean activo
    );


    RegistroKilometrajeSalidaDTO obtenerPorId(
            Long id
    );


    RegistroKilometrajeSalidaDTO obtenerActual(
            Long vehiculoId
    );


    RegistroKilometrajeSalidaDTO crear(
            RegistroKilometrajeGuardarDTO registroGuardar
    );


    RegistroKilometrajeSalidaDTO editar(
            RegistroKilometrajeModificarDTO registroModificar
    );


    void eliminarPorId(
            Long id
    );
}