package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.esfe.dtos.Vehiculo.VehiculoGuardarDTO;
import org.esfe.dtos.Vehiculo.VehiculoModificarDTO;
import org.esfe.dtos.Vehiculo.VehiculoSalidaDTO;
import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.esfe.enums.EstadoVehiculo;
import org.esfe.modelos.Vehiculo;
import org.esfe.repositorios.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.esfe.servicio.interfaces.IVehiculoService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class VehiculoServiceImpl implements IVehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoSalidaDTO> obtenerTodosPaginados(Pageable pageable)
    {
        return vehiculoRepository
                .findAll(pageable)
                .map(this::convertirASalida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoSalidaDTO> obtenerTodos()
    {
        return vehiculoRepository
                .findAll()
                .stream()
                .map(this::convertirASalida)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoSalidaDTO obtenerPorId(Long id)
    {
        Vehiculo vehiculo =
                buscarEntidadPorId(id);

        return convertirASalida(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoSalidaDTO crear(VehiculoGuardarDTO dto)
    {
        String placa = normalizarPlaca(dto.getPlaca());
        validarPlacaNueva(placa);

        String vin = normalizarVin(dto.getVin());
        validarVinNuevo(vin);

        Vehiculo vehiculo = new Vehiculo();

        vehiculo.setPropietarioId(dto.getPropietarioId());

        vehiculo.setMarca(normalizarTexto(dto.getMarca()));

        vehiculo.setModelo(normalizarTexto(dto.getModelo()));

        vehiculo.setAnio(dto.getAnio());

        vehiculo.setPlaca(placa);

        vehiculo.setColor(normalizarOpcional(dto.getColor()));

        vehiculo.setKilometrajeActual(dto.getKilometrajeActual());

        vehiculo.setVin(vin);

        vehiculo.setMotor(normalizarOpcional(dto.getMotor()));

        vehiculo.setEstado(EstadoVehiculo.ACTIVO);

        Vehiculo guardado = vehiculoRepository.save(vehiculo);

        return convertirASalida(guardado);
    }

    @Override
    @Transactional
    public VehiculoSalidaDTO editar(VehiculoModificarDTO dto)
    {
        Vehiculo vehiculo = buscarEntidadPorId(dto.getId());

        String placa = normalizarPlaca(dto.getPlaca());

        if (vehiculoRepository.existsByPlacaIgnoreCaseAndIdNot(placa, dto.getId()))
        {
            throw new IllegalArgumentException
                    ("Ya existe otro vehículo registrado con la placa " + placa);
        }

        String vin = normalizarVin(dto.getVin());

        if (vin != null && vehiculoRepository.existsByVinIgnoreCaseAndIdNot(vin, dto.getId()))
        {
            throw new IllegalArgumentException
                    ("Ya existe otro vehículo registrado con el VIN " + vin);
        }

        vehiculo.setMarca(normalizarTexto(dto.getMarca()));

        vehiculo.setModelo(normalizarTexto(dto.getModelo()));

        vehiculo.setAnio(dto.getAnio());

        vehiculo.setPlaca(placa);

        vehiculo.setColor(normalizarOpcional(dto.getColor()));

        vehiculo.setKilometrajeActual(dto.getKilometrajeActual());

        vehiculo.setVin(vin);

        vehiculo.setMotor(normalizarOpcional(dto.getMotor()));

        vehiculo.setEstado(dto.getEstado());

        Vehiculo actualizado = vehiculoRepository.save(vehiculo);

        return convertirASalida(actualizado);
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {

        Vehiculo vehiculo =
                buscarEntidadPorId(id);

        /*
         * Eliminación lógica.
         * No borramos físicamente la fila.
         */
        vehiculo.setEstado(
                EstadoVehiculo.ARCHIVADO
        );

        vehiculoRepository.save(vehiculo);
    }

    private Vehiculo buscarEntidadPorId(
            Long id
    ) {

        return vehiculoRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "No se encontró el vehículo con ID "
                                        + id
                        )
                );
    }

    private void validarPlacaNueva(
            String placa
    ) {

        if (
                vehiculoRepository
                        .existsByPlacaIgnoreCase(placa)
        ) {

            throw new IllegalArgumentException(
                    "Ya existe un vehículo registrado con la placa "
                            + placa
            );
        }
    }


    private void validarVinNuevo(
            String vin
    ) {

        if (
                vin != null &&
                        vehiculoRepository
                                .existsByVinIgnoreCase(vin)
        ) {

            throw new IllegalArgumentException(
                    "Ya existe un vehículo registrado con el VIN "
                            + vin
            );
        }
    }


    private String normalizarTexto(
            String valor
    ) {

        return valor.trim();
    }


    private String normalizarPlaca(
            String placa
    ) {

        return placa
                .trim()
                .toUpperCase();
    }


    private String normalizarVin(
            String vin
    ) {

        if (
                vin == null ||
                        vin.isBlank()
        ) {
            return null;
        }

        return vin
                .trim()
                .toUpperCase();
    }


    private String normalizarOpcional(
            String valor
    ) {

        if (
                valor == null ||
                        valor.isBlank()
        ) {
            return null;
        }

        return valor.trim();
    }


    private VehiculoSalidaDTO convertirASalida(
            Vehiculo vehiculo
    ) {

        VehiculoSalidaDTO dto =
                new VehiculoSalidaDTO();

        dto.setId(
                vehiculo.getId()
        );

        dto.setPropietarioId(
                vehiculo.getPropietarioId()
        );

        dto.setMarca(
                vehiculo.getMarca()
        );

        dto.setModelo(
                vehiculo.getModelo()
        );

        dto.setAnio(
                vehiculo.getAnio()
        );

        dto.setPlaca(
                vehiculo.getPlaca()
        );

        dto.setColor(
                vehiculo.getColor()
        );

        dto.setKilometrajeActual(
                vehiculo.getKilometrajeActual()
        );

        dto.setVin(
                vehiculo.getVin()
        );

        dto.setMotor(
                vehiculo.getMotor()
        );

        dto.setEstado(
                vehiculo.getEstado()
        );

        dto.setFechaRegistro(
                vehiculo.getFechaRegistro()
        );

        dto.setFechaActualizacion(
                vehiculo.getFechaActualizacion()
        );

        return dto;
    }

}