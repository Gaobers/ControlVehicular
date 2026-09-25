package org.esfe.servicio.implementaciones;

import org.esfe.dtos.RespuestaDTO;
import org.esfe.modelos.vehiculo;
import org.esfe.repositorios.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<vehiculo> obtenerPorCliente(Long clienteId) {
        return vehiculoRepository.findByClienteId(clienteId);
    }

    public RespuestaDTO<vehiculo> actualizarKilometraje(Long id, Double nuevoKilometraje) {

        vehiculo vehiculo = vehiculoRepository.findById(id).orElse(null);

        if (vehiculo == null) {
            return new RespuestaDTO<>(
                    false,
                    "Vehículo no encontrado",
                    null
            );
        }

        if (nuevoKilometraje < vehiculo.getKilometrajeActual()) {
            return new RespuestaDTO<>(
                    false,
                    "El nuevo kilometraje (" + nuevoKilometraje +
                            ") no puede ser menor al actual (" +
                            vehiculo.getKilometrajeActual() + ")",
                    null
            );
        }

        vehiculo.setKilometrajeActual(nuevoKilometraje);
        vehiculoRepository.save(vehiculo);

        return new RespuestaDTO<>(
                true,
                "Kilometraje actualizado correctamente",
                vehiculo
        );
    }
}