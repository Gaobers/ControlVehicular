package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;

import org.esfe.dtos.kilometraje.RegistroKilometrajeGuardarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeModificarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeSalidaDTO;

import org.esfe.modelos.RegistroKilometraje;

import org.esfe.repositorios.RegistroKilometrajeRepository;

import org.esfe.servicio.interfaces.IRegistroKilometrajeService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class RegistroKilometrajeServiceImpl
        implements IRegistroKilometrajeService {


    private final RegistroKilometrajeRepository
            registroKilometrajeRepository;


    public RegistroKilometrajeServiceImpl(
            RegistroKilometrajeRepository registroKilometrajeRepository
    ) {

        this.registroKilometrajeRepository =
                registroKilometrajeRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<RegistroKilometrajeSalidaDTO>
    obtenerTodosPaginados(

            Long vehiculoId,

            Boolean activo,

            Pageable pageable
    ) {

        return registroKilometrajeRepository
                .buscarPorFiltrosPaginados(
                        vehiculoId,
                        activo,
                        pageable
                )
                .map(
                        this::convertirASalida
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<RegistroKilometrajeSalidaDTO>
    obtenerTodos(

            Long vehiculoId,

            Boolean activo
    ) {

        return registroKilometrajeRepository
                .buscarPorFiltros(
                        vehiculoId,
                        activo
                )
                .stream()
                .map(
                        this::convertirASalida
                )
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public RegistroKilometrajeSalidaDTO obtenerPorId(
            Long id
    ) {

        RegistroKilometraje registro =
                buscarEntidadPorId(id);


        return convertirASalida(
                registro
        );
    }


    @Override
    @Transactional(readOnly = true)
    public RegistroKilometrajeSalidaDTO obtenerActual(
            Long vehiculoId
    ) {

        RegistroKilometraje registro =
                registroKilometrajeRepository
                        .findTopByVehiculoIdAndActivoTrueOrderByKilometrajeDescFechaHoraDesc(
                                vehiculoId
                        )
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró kilometraje activo para el vehículo con id: "
                                                        + vehiculoId
                                        )
                        );


        return convertirASalida(
                registro
        );
    }


    @Override
    public RegistroKilometrajeSalidaDTO crear(
            RegistroKilometrajeGuardarDTO dto
    ) {

        validarNuevoKilometraje(
                dto.getVehiculoId(),
                dto.getKilometraje()
        );


        RegistroKilometraje registro =
                new RegistroKilometraje();


        registro.setVehiculoId(
                dto.getVehiculoId()
        );


        /*
         * Posteriormente este valor puede
         * obtenerse directamente del JWT.
         */
        registro.setRegistradoPor(null);


        registro.setKilometraje(
                dto.getKilometraje()
        );


        registro.setObservacion(
                normalizarObservacion(
                        dto.getObservacion()
                )
        );


        registro.setActivo(true);


        RegistroKilometraje guardado =
                registroKilometrajeRepository
                        .save(registro);


        return convertirASalida(
                guardado
        );
    }


    @Override
    public RegistroKilometrajeSalidaDTO editar(
            RegistroKilometrajeModificarDTO dto
    ) {

        RegistroKilometraje registro =
                buscarEntidadPorId(
                        dto.getId()
                );


        /*
         * Conservamos la regla del módulo
         * original: al editar una lectura,
         * no se permite disminuir su valor.
         */
        if (
                dto.getKilometraje()
                        .compareTo(
                                registro.getKilometraje()
                        ) < 0
        ) {

            throw new IllegalArgumentException(
                    "El nuevo kilometraje ("
                            + dto.getKilometraje()
                            + ") no puede ser menor al kilometraje registrado actualmente ("
                            + registro.getKilometraje()
                            + ")"
            );
        }


        registro.setKilometraje(
                dto.getKilometraje()
        );


        registro.setObservacion(
                normalizarObservacion(
                        dto.getObservacion()
                )
        );


        registro.setActivo(
                dto.getActivo()
        );


        RegistroKilometraje actualizado =
                registroKilometrajeRepository
                        .save(registro);


        return convertirASalida(
                actualizado
        );
    }


    @Override
    public void eliminarPorId(
            Long id
    ) {

        RegistroKilometraje registro =
                buscarEntidadPorId(id);


        /*
         * BORRADO LÓGICO.
         * El historial nunca se elimina
         * físicamente.
         */
        registro.setActivo(false);


        registroKilometrajeRepository
                .save(registro);
    }


    private void validarNuevoKilometraje(

            Long vehiculoId,

            BigDecimal nuevoKilometraje
    ) {

        Optional<RegistroKilometraje>
                kilometrajeActual =
                registroKilometrajeRepository
                        .findTopByVehiculoIdAndActivoTrueOrderByKilometrajeDescFechaHoraDesc(
                                vehiculoId
                        );


        if (kilometrajeActual.isEmpty()) {
            return;
        }


        RegistroKilometraje actual =
                kilometrajeActual.get();


        if (
                nuevoKilometraje
                        .compareTo(
                                actual.getKilometraje()
                        ) < 0
        ) {

            throw new IllegalArgumentException(
                    "El kilometraje ("
                            + nuevoKilometraje
                            + ") no puede ser menor al último kilometraje registrado ("
                            + actual.getKilometraje()
                            + ")"
            );
        }
    }


    private RegistroKilometraje
    buscarEntidadPorId(
            Long id
    ) {

        return registroKilometrajeRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Registro de kilometraje no encontrado con id: "
                                                + id
                                )
                );
    }


    private String normalizarObservacion(
            String observacion
    ) {

        if (
                observacion == null ||
                        observacion.isBlank()
        ) {

            return null;
        }


        return observacion.trim();
    }


    private RegistroKilometrajeSalidaDTO
    convertirASalida(
            RegistroKilometraje registro
    ) {

        RegistroKilometrajeSalidaDTO salida =
                new RegistroKilometrajeSalidaDTO();


        salida.setId(
                registro.getId()
        );


        salida.setVehiculoId(
                registro.getVehiculoId()
        );


        salida.setRegistradoPor(
                registro.getRegistradoPor()
        );


        salida.setKilometraje(
                registro.getKilometraje()
        );


        salida.setFechaHora(
                registro.getFechaHora()
        );


        salida.setObservacion(
                registro.getObservacion()
        );


        salida.setActivo(
                registro.getActivo()
        );


        return salida;
    }
}