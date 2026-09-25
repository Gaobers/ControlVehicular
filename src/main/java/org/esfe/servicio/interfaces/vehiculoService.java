package org.esfe.servicio.interfaces;

import jakarta.validation.Valid;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;

import java.util.List;

public interface vehiculoService {

    VehiculoResponse registrarVehiculo(@Valid VehiculoRegistroRequest request, Long usuarioAutenticadoId, String rolUsuario);

    VehiculoResponse obtenerPorId(Long id);

    VehiculoResponse actualizarVehiculo(Long id, VehiculoRegistroRequest request, Long usuarioAutenticadoId, String rolUsuario);

    void eliminarVehiculo(Long id, Long usuarioAutenticadoId, String rolUsuario);

    List listarTodos();

}