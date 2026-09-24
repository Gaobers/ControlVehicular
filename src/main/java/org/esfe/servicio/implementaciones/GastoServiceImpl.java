package org.esfe.servicio.implementaciones;

import org.esfe.dtos.GastoRegistroRequest;
import org.esfe.dtos.GastoResponse;
import org.esfe.modelos.Gasto;
import org.esfe.repositorios.GastoRepository;
import org.esfe.servicio.interfaces.IGastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements IGastoService {

    private final GastoRepository gastoRepository;

    @Override
    public GastoResponse registrarGasto(GastoRegistroRequest request) {
        Gasto gasto = Gasto.builder()
                .vehiculoId(request.getVehiculoId())
                .categoria(request.getCategoria())
                .monto(request.getMonto())
                .fecha(request.getFecha())
                .descripcion(request.getDescripcion())
                .build();

        Gasto guardado = gastoRepository.save(gasto);
        return mapToResponse(guardado);
    }

    @Override
    public GastoResponse obtenerPorId(Long id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con id: " + id));
        return mapToResponse(gasto);
    }

    @Override
    public List<GastoResponse> obtenerTodos() {
        return gastoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GastoResponse actualizarGasto(Long id, GastoRegistroRequest request) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con id: " + id));

        gasto.setVehiculoId(request.getVehiculoId());
        gasto.setCategoria(request.getCategoria());
        gasto.setMonto(request.getMonto());
        gasto.setFecha(request.getFecha());
        gasto.setDescripcion(request.getDescripcion());

        Gasto actualizado = gastoRepository.save(gasto);
        return mapToResponse(actualizado);
    }

    @Override
    public void eliminarGasto(Long id) {
        gastoRepository.deleteById(id);
    }

    @Override
    public List<GastoResponse> obtenerAuxiliar() {
        return obtenerTodos();
    }

    private GastoResponse mapToResponse(Gasto gasto) {
        return GastoResponse.builder()
                .id(gasto.getId())
                .vehiculoId(gasto.getVehiculoId())
                .categoria(gasto.getCategoria())
                .monto(gasto.getMonto())
                .fecha(gasto.getFecha())
                .descripcion(gasto.getDescripcion())
                .build();
    }
}