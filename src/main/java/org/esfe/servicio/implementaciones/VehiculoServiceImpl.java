package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.esfe.modelos.vehiculo;
import org.esfe.repositorios.VehiculoRepository;
import org.esfe.servicio.interfaces.vehiculoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoServiceImpl implements vehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public VehiculoResponse registrarVehiculo(
            @Valid VehiculoRegistroRequest request,
            Long usuarioAutenticadoId,
            String rolUsuario) {

        vehiculo nuevoVehiculo = new vehiculo();

        nuevoVehiculo.setMarca(request.getMarca());
        nuevoVehiculo.setModelo(request.getModelo());
        nuevoVehiculo.setAnio(request.getAnio());
        nuevoVehiculo.setPlaca(request.getPlaca());
        nuevoVehiculo.setKilometrajeActual(request.getKilometrajeActual());
        nuevoVehiculo.setCaracteristicas(request.getCaracteristicas());
        nuevoVehiculo.setDocumentacion(request.getDocumentacion());
        nuevoVehiculo.setFechaVencimientoDocumentacion(
                request.getFechaVencimientoDocumentacion()
        );

        /*
         * Si registra un administrador, puede indicar a qué cliente
         * pertenece el vehículo.
         *
         * Si registra un cliente, automáticamente se utiliza su propio ID.
         */
        if (esAdministrador(rolUsuario)) {

            if (request.getClienteId() == null) {
                throw new IllegalArgumentException(
                        "Debe indicar el cliente al registrar el vehículo"
                );
            }

            nuevoVehiculo.setClienteId(request.getClienteId());

        } else {

            nuevoVehiculo.setClienteId(usuarioAutenticadoId);
        }

        vehiculo guardado = vehiculoRepository.save(nuevoVehiculo);

        VehiculoResponse response = convertirAResponse(guardado);
        response.setMensaje("Vehículo registrado correctamente");

        return response;
    }

    @Override
    public VehiculoResponse obtenerPorId(Long id) {

        vehiculo encontrado = vehiculoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Vehículo no encontrado con ID: " + id
                        )
                );

        return convertirAResponse(encontrado);
    }

    @Override
    public VehiculoResponse actualizarVehiculo(
            Long id,
            VehiculoRegistroRequest request,
            Long usuarioAutenticadoId,
            String rolUsuario) {

        vehiculo encontrado = vehiculoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Vehículo no encontrado con ID: " + id
                        )
                );

        validarPermiso(
                encontrado,
                usuarioAutenticadoId,
                rolUsuario
        );

        encontrado.setMarca(request.getMarca());
        encontrado.setModelo(request.getModelo());
        encontrado.setAnio(request.getAnio());
        encontrado.setPlaca(request.getPlaca());
        encontrado.setKilometrajeActual(request.getKilometrajeActual());
        encontrado.setCaracteristicas(request.getCaracteristicas());
        encontrado.setDocumentacion(request.getDocumentacion());
        encontrado.setFechaVencimientoDocumentacion(
                request.getFechaVencimientoDocumentacion()
        );

        /*
         * Solo el administrador puede cambiar el propietario
         * del vehículo.
         */
        if (esAdministrador(rolUsuario)
                && request.getClienteId() != null) {

            encontrado.setClienteId(request.getClienteId());
        }

        vehiculo actualizado = vehiculoRepository.save(encontrado);

        VehiculoResponse response = convertirAResponse(actualizado);
        response.setMensaje("Vehículo actualizado correctamente");

        return response;
    }

    @Override
    public void eliminarVehiculo(
            Long id,
            Long usuarioAutenticadoId,
            String rolUsuario) {

        vehiculo encontrado = vehiculoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Vehículo no encontrado con ID: " + id
                        )
                );

        validarPermiso(
                encontrado,
                usuarioAutenticadoId,
                rolUsuario
        );

        vehiculoRepository.delete(encontrado);
    }

    @Override
    public List<VehiculoResponse> listarTodos() {

        return vehiculoRepository
                .findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    public List<VehiculoResponse> obtenerListaAuxiliar() {

        return vehiculoRepository
                .findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private VehiculoResponse convertirAResponse(vehiculo vehiculo) {

        VehiculoResponse response = new VehiculoResponse();

        response.setId(vehiculo.getId());
        response.setMarca(vehiculo.getMarca());
        response.setModelo(vehiculo.getModelo());
        response.setAnio(vehiculo.getAnio());
        response.setPlaca(vehiculo.getPlaca());
        response.setKilometrajeActual(
                vehiculo.getKilometrajeActual()
        );
        response.setCaracteristicas(
                vehiculo.getCaracteristicas()
        );
        response.setDocumentacion(
                vehiculo.getDocumentacion()
        );
        response.setFechaVencimientoDocumentacion(
                vehiculo.getFechaVencimientoDocumentacion()
        );
        response.setClienteId(
                vehiculo.getClienteId()
        );

        return response;
    }

    private void validarPermiso(
            vehiculo vehiculo,
            Long usuarioAutenticadoId,
            String rolUsuario) {

        if (esAdministrador(rolUsuario)) {
            return;
        }

        if (!vehiculo.getClienteId()
                .equals(usuarioAutenticadoId)) {

            throw new IllegalArgumentException(
                    "No tiene permisos para modificar este vehículo"
            );
        }
    }

    private boolean esAdministrador(String rolUsuario) {

        if (rolUsuario == null) {
            return false;
        }

        return rolUsuario.equalsIgnoreCase("ADMINISTRADOR")
                || rolUsuario.equalsIgnoreCase("ADMIN")
                || rolUsuario.equalsIgnoreCase("ROLE_ADMIN");
    }
}