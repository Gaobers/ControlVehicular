package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;

import org.esfe.enums.EstadoMantenimiento;

import org.esfe.modelos.Mantenimiento;

import org.esfe.repositorios.MantenimientoRepository;

import org.esfe.servicio.interfaces.IMantenimientoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class MantenimientoServiceImpl
        implements IMantenimientoService {


    private final MantenimientoRepository
            mantenimientoRepository;


    public MantenimientoServiceImpl(
            MantenimientoRepository mantenimientoRepository
    ) {

        this.mantenimientoRepository =
                mantenimientoRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<MantenimientoSalidaDTO>
    obtenerTodosPaginados(

            Long vehiculoId,

            EstadoMantenimiento estado,

            Boolean activo,

            Pageable pageable
    ) {

        return mantenimientoRepository
                .buscarPorFiltrosPaginados(
                        vehiculoId,
                        estado,
                        activo,
                        pageable
                )
                .map(
                        this::convertirASalida
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoSalidaDTO>
    obtenerTodos(

            Long vehiculoId,

            EstadoMantenimiento estado,

            Boolean activo
    ) {

        return mantenimientoRepository
                .buscarPorFiltros(
                        vehiculoId,
                        estado,
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
    public MantenimientoSalidaDTO obtenerPorId(
            Long id
    ) {

        Mantenimiento mantenimiento =
                buscarEntidadPorId(id);


        return convertirASalida(
                mantenimiento
        );
    }


    @Override
    public MantenimientoSalidaDTO crear(
            MantenimientoGuardarDTO dto
    ) {

        validarObjetivo(
                dto.getFechaObjetivo(),
                dto.getKilometrajeObjetivo()
        );


        Mantenimiento mantenimiento =
                new Mantenimiento();


        mantenimiento.setVehiculoId(
                dto.getVehiculoId()
        );


        /*
         * Posteriormente puede tomarse
         * directamente del usuario autenticado.
         */
        mantenimiento.setCreadoPor(null);


        mantenimiento.setMantenimientoOrigenId(
                dto.getMantenimientoOrigenId()
        );


        mantenimiento.setServicio(
                dto.getServicio().trim()
        );


        mantenimiento.setObservaciones(
                normalizarTextoOpcional(
                        dto.getObservaciones()
                )
        );


        mantenimiento.setFechaObjetivo(
                dto.getFechaObjetivo()
        );


        mantenimiento.setKilometrajeObjetivo(
                dto.getKilometrajeObjetivo()
        );


        mantenimiento.setEstado(
                dto.getEstado()
        );


        mantenimiento.setFechaRealizacion(
                dto.getFechaRealizacion()
        );


        mantenimiento.setKilometrajeRealizacion(
                dto.getKilometrajeRealizacion()
        );


        mantenimiento.setActivo(true);


        Mantenimiento guardado =
                mantenimientoRepository.save(
                        mantenimiento
                );


        return convertirASalida(
                guardado
        );
    }


    @Override
    public MantenimientoSalidaDTO editar(
            MantenimientoModificarDTO dto
    ) {

        validarObjetivo(
                dto.getFechaObjetivo(),
                dto.getKilometrajeObjetivo()
        );


        Mantenimiento mantenimiento =
                buscarEntidadPorId(
                        dto.getId()
                );


        mantenimiento.setVehiculoId(
                dto.getVehiculoId()
        );


        mantenimiento.setMantenimientoOrigenId(
                dto.getMantenimientoOrigenId()
        );


        mantenimiento.setServicio(
                dto.getServicio().trim()
        );


        mantenimiento.setObservaciones(
                normalizarTextoOpcional(
                        dto.getObservaciones()
                )
        );


        mantenimiento.setFechaObjetivo(
                dto.getFechaObjetivo()
        );


        mantenimiento.setKilometrajeObjetivo(
                dto.getKilometrajeObjetivo()
        );


        mantenimiento.setEstado(
                dto.getEstado()
        );


        mantenimiento.setActivo(
                dto.getActivo()
        );


        mantenimiento.setFechaRealizacion(
                dto.getFechaRealizacion()
        );


        mantenimiento.setKilometrajeRealizacion(
                dto.getKilometrajeRealizacion()
        );


        Mantenimiento actualizado =
                mantenimientoRepository.save(
                        mantenimiento
                );


        return convertirASalida(
                actualizado
        );
    }


    @Override
    public void eliminarPorId(
            Long id
    ) {

        Mantenimiento mantenimiento =
                buscarEntidadPorId(id);


        /*
         * BORRADO LÓGICO.
         * Nunca eliminamos físicamente
         * el historial.
         */
        mantenimiento.setActivo(false);


        mantenimientoRepository.save(
                mantenimiento
        );
    }


    private Mantenimiento buscarEntidadPorId(
            Long id
    ) {

        return mantenimientoRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Mantenimiento no encontrado con id: "
                                                + id
                                )
                );
    }


    private void validarObjetivo(

            LocalDate fechaObjetivo,

            BigDecimal kilometrajeObjetivo
    ) {

        if (
                fechaObjetivo == null &&
                        kilometrajeObjetivo == null
        ) {

            throw new IllegalArgumentException(
                    "Debe indicar fechaObjetivo o kilometrajeObjetivo"
            );
        }
    }


    private String normalizarTextoOpcional(
            String texto
    ) {

        if (
                texto == null ||
                        texto.isBlank()
        ) {

            return null;
        }


        return texto.trim();
    }


    private MantenimientoSalidaDTO convertirASalida(
            Mantenimiento mantenimiento
    ) {

        MantenimientoSalidaDTO salida =
                new MantenimientoSalidaDTO();


        salida.setId(
                mantenimiento.getId()
        );


        salida.setVehiculoId(
                mantenimiento.getVehiculoId()
        );


        salida.setCreadoPor(
                mantenimiento.getCreadoPor()
        );


        salida.setMantenimientoOrigenId(
                mantenimiento
                        .getMantenimientoOrigenId()
        );


        salida.setServicio(
                mantenimiento.getServicio()
        );


        salida.setObservaciones(
                mantenimiento.getObservaciones()
        );


        salida.setFechaObjetivo(
                mantenimiento.getFechaObjetivo()
        );


        salida.setKilometrajeObjetivo(
                mantenimiento
                        .getKilometrajeObjetivo()
        );


        salida.setEstado(
                mantenimiento.getEstado()
        );


        salida.setActivo(
                mantenimiento.getActivo()
        );


        salida.setFechaRealizacion(
                mantenimiento.getFechaRealizacion()
        );


        salida.setKilometrajeRealizacion(
                mantenimiento
                        .getKilometrajeRealizacion()
        );


        salida.setFechaCreacion(
                mantenimiento.getFechaCreacion()
        );


        salida.setFechaActualizacion(
                mantenimiento.getFechaActualizacion()
        );


        return salida;
    }
}