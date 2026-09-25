package org.esfe.servicio.implementaciones;

import org.esfe.dtos.RespuestaDTO;
import org.esfe.modelos.HistorialKilometraje;
import org.esfe.repositorios.HistorialKilometrajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KilometrajeService {

    private final HistorialKilometrajeRepository historialKilometrajeRepository;

    public KilometrajeService(HistorialKilometrajeRepository historialKilometrajeRepository) {
        this.historialKilometrajeRepository = historialKilometrajeRepository;
    }

    public RespuestaDTO<HistorialKilometraje> registrar(HistorialKilometraje entidad) {
        validarKilometraje(entidad.getVehiculoId(), entidad.getKilometraje(), null);

        HistorialKilometraje guardado = historialKilometrajeRepository.save(entidad);
        return new RespuestaDTO<>(true, "Kilometraje registrado correctamente", guardado);
    }

    public RespuestaDTO<List<HistorialKilometraje>> obtenerHistorial(Long vehiculoId) {
        List<HistorialKilometraje> historial = historialKilometrajeRepository.findByVehiculoIdOrderByFechaRegistroDesc(vehiculoId);
        return new RespuestaDTO<>(true, "Historial encontrado", historial);
    }

    public RespuestaDTO<HistorialKilometraje> obtenerActual(Long vehiculoId) {
        Optional<HistorialKilometraje> actual = historialKilometrajeRepository.findTopByVehiculoIdOrderByKilometrajeDesc(vehiculoId);
        if (actual.isPresent()) {
            return new RespuestaDTO<>(true, "Kilometraje actual encontrado", actual.get());
        }
        return new RespuestaDTO<>(false, "No se encontró kilometraje para el vehículo " + vehiculoId, null);
    }

    public RespuestaDTO<HistorialKilometraje> obtenerPorId(Long id) {
        Optional<HistorialKilometraje> historial = historialKilometrajeRepository.findById(id);
        if (historial.isPresent()) {
            return new RespuestaDTO<>(true, "Registro encontrado", historial.get());
        }
        return new RespuestaDTO<>(false, "Registro de kilometraje no encontrado con ID: " + id, null);
    }

    public RespuestaDTO<HistorialKilometraje> actualizar(Long id, HistorialKilometraje entidad) {
        Optional<HistorialKilometraje> existenteOpt = historialKilometrajeRepository.findById(id);
        if (existenteOpt.isEmpty()) {
            return new RespuestaDTO<>(false, "Registro de kilometraje no encontrado con ID: " + id, null);
        }

        HistorialKilometraje existente = existenteOpt.get();

        if (entidad.getKilometraje() < existente.getKilometraje()) {
            return new RespuestaDTO<>(false,
                    "El nuevo kilometraje (" + entidad.getKilometraje() + ") no puede ser menor al actual (" + existente.getKilometraje() + ")",
                    null);
        }

        existente.setKilometraje(entidad.getKilometraje());
        existente.setObservaciones(entidad.getObservaciones());

        HistorialKilometraje actualizado = historialKilometrajeRepository.save(existente);
        return new RespuestaDTO<>(true, "Kilometraje actualizado correctamente", actualizado);
    }

    public RespuestaDTO<Void> eliminar(Long id) {
        if (!historialKilometrajeRepository.existsById(id)) {
            return new RespuestaDTO<>(false, "Registro de kilometraje no encontrado con ID: " + id, null);
        }
        historialKilometrajeRepository.deleteById(id);
        return new RespuestaDTO<>(true, "Registro de kilometraje eliminado correctamente", null);
    }

    private void validarKilometraje(Long vehiculoId, Integer nuevoKilometraje, Long excluirId) {
        Optional<HistorialKilometraje> maxKmOpt = historialKilometrajeRepository.findTopByVehiculoIdOrderByKilometrajeDesc(vehiculoId);
        if (maxKmOpt.isPresent()) {
            HistorialKilometraje maxKm = maxKmOpt.get();
            if (excluirId != null && maxKm.getId().equals(excluirId)) {
                return;
            }
            if (nuevoKilometraje < maxKm.getKilometraje()) {
                throw new IllegalArgumentException(
                        "El kilometraje (" + nuevoKilometraje + ") no puede ser menor al último registrado (" + maxKm.getKilometraje() + ")");
            }
        }
    }
}