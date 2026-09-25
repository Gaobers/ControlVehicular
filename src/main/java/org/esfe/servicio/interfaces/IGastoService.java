package org.esfe.servicio.interfaces;

import org.esfe.dtos.GastoRegistroRequest;
import org.esfe.dtos.GastoResponse;

import java.util.List;

public interface IGastoService {
    GastoResponse registrarGasto(GastoRegistroRequest request);
    GastoResponse obtenerPorId(Long id);
    List<GastoResponse> obtenerTodos();
    GastoResponse actualizarGasto(Long id, GastoRegistroRequest request);
    void eliminarGasto(Long id);
    List<GastoResponse> obtenerAuxiliar();
}