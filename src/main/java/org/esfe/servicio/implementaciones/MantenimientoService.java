package org.esfe.servicio.implementaciones;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;
import org.esfe.enums.EstadoMantenimiento;
import org.esfe.modelos.Mantenimiento;
import org.esfe.repositorios.IMantenimientoRepository;
import org.esfe.servicio.interfaces.IMantenimientoService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MantenimientoService implements IMantenimientoService {

    @Autowired
    private IMantenimientoRepository mantenimientoRepository;

    @Override
    public List<MantenimientoSalidaDTO> obtenerTodos(
            Long vehiculoId,
            EstadoMantenimiento estado) {

        List<Mantenimiento> lista =
                mantenimientoRepository.buscarPorFiltros(
                        vehiculoId,
                        estado
                );

        return lista.stream()
                .map(this::convertirASalida)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MantenimientoSalidaDTO> obtenerPorId(Long id) {
        return mantenimientoRepository.findById(id)
                .map(this::convertirASalida);
    }

    @Override
    public MantenimientoSalidaDTO crear(@NonNull MantenimientoGuardarDTO dto) {
        Mantenimiento entidad = new Mantenimiento();

        entidad.setVehiculoId(dto.getVehiculoId());
        entidad.setServicio(dto.getServicio());
        entidad.setEstado(dto.getEstado());

        Mantenimiento guardado = mantenimientoRepository.save(entidad);

        return convertirASalida(guardado);
    }

    @Override
    public MantenimientoSalidaDTO modificar(@NonNull MantenimientoModificarDTO dto) {
        Mantenimiento entidad = mantenimientoRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        entidad.setVehiculoId(dto.getVehiculoId());
        entidad.setServicio(dto.getServicio());
        entidad.setEstado(dto.getEstado());

        Mantenimiento modificado = mantenimientoRepository.save(entidad);

        return convertirASalida(modificado);
    }

    @Override
    public boolean eliminarPorId(Long id) {
        if (mantenimientoRepository.findById(id).isPresent()) {
            mantenimientoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private @NonNull MantenimientoSalidaDTO convertirASalida(
            @NonNull Mantenimiento m) {

        MantenimientoSalidaDTO salida = new MantenimientoSalidaDTO();

        salida.setId(m.getId());
        salida.setVehiculoId(m.getVehiculoId());
        salida.setServicio(m.getServicio());
        salida.setEstado(m.getEstado());

        return salida;
    }
}