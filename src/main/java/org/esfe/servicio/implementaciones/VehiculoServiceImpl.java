package org.esfe.servicio.implementaciones;

import jakarta.validation.Valid;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.springframework.stereotype.Service;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.esfe.servicio.interfaces.vehiculoService;

import java.util.Collections;
import java.util.List;

@Service
public class VehiculoServiceImpl implements vehiculoService {

    @Override
    public VehiculoResponse registrarVehiculo(@Valid VehiculoRegistroRequest request, Long usuarioAutenticadoId, String rolUsuario) {
        return new VehiculoResponse();
    }

    @Override
    public VehiculoResponse obtenerPorId(Long id) {
        return new VehiculoResponse();
    }

    @Override
    public VehiculoResponse actualizarVehiculo(Long id, VehiculoRegistroRequest request, Long usuarioAutenticadoId, String rolUsuario) {
        return new VehiculoResponse();
    }

    @Override
    public void eliminarVehiculo(Long id, Long usuarioAutenticadoId, String rolUsuario) {
    }

    @Override
    public List listarTodos() {
        return Collections.emptyList();
    }

    @Override
    public List obtenerListaAuxiliar() {
        return Collections.emptyList();
    }
}