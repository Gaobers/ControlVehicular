package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;

import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoGuardar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoModificar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoSalida;

import org.esfe.modelos.RecordatorioMantenimiento;

import org.esfe.repositorios.MantenimientoRepository;
import org.esfe.repositorios.RecordatorioMantenimientoRepository;

import org.esfe.servicio.interfaces.IRecordatorioMantenimientoService;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class RecordatorioMantenimientoService
        implements IRecordatorioMantenimientoService {


    @Autowired
    private RecordatorioMantenimientoRepository
            recordatorioRepository;


    @Autowired
    private MantenimientoRepository
            mantenimientoRepository;


    @Autowired
    private ModelMapper
            modelMapper;


    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioMantenimientoSalida>
    obtenerTodos() {

        List<RecordatorioMantenimiento> recordatorios =
                recordatorioRepository.findAll();


        return recordatorios
                .stream()
                .map(
                        recordatorio ->
                                modelMapper.map(
                                        recordatorio,
                                        RecordatorioMantenimientoSalida.class
                                )
                )
                .collect(
                        Collectors.toList()
                );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<RecordatorioMantenimientoSalida>
    obtenerTodosPaginados(
            Pageable pageable
    ) {

        Page<RecordatorioMantenimiento> page =
                recordatorioRepository.findAll(
                        pageable
                );


        List<RecordatorioMantenimientoSalida>
                recordatoriosDto =
                page.stream()
                        .map(
                                recordatorio ->
                                        modelMapper.map(
                                                recordatorio,
                                                RecordatorioMantenimientoSalida.class
                                        )
                        )
                        .collect(
                                Collectors.toList()
                        );


        return new PageImpl<>(
                recordatoriosDto,
                page.getPageable(),
                page.getTotalElements()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public RecordatorioMantenimientoSalida obtenerPorId(
            Long id
    ) {

        RecordatorioMantenimiento recordatorio =
                buscarEntidadPorId(
                        id
                );


        return modelMapper.map(
                recordatorio,
                RecordatorioMantenimientoSalida.class
        );
    }


    @Override
    public RecordatorioMantenimientoSalida crear(
            RecordatorioMantenimientoGuardar recordatorioGuardar
    ) {

        /*
         * Validamos que el mantenimiento
         * relacionado realmente exista.
         */
        if (
                !mantenimientoRepository.existsById(
                        recordatorioGuardar
                                .getMantenimientoId()
                )
        ) {

            throw new IllegalArgumentException(
                    "El mantenimiento seleccionado no existe"
            );
        }


        RecordatorioMantenimiento recordatorio =
                new RecordatorioMantenimiento();


        recordatorio.setMantenimientoId(
                recordatorioGuardar
                        .getMantenimientoId()
        );


        if (
                recordatorioGuardar
                        .getDiasAnticipacion()
                        != null
        ) {

            recordatorio.setDiasAnticipacion(
                    recordatorioGuardar
                            .getDiasAnticipacion()
            );

        } else {

            recordatorio.setDiasAnticipacion(
                    15
            );
        }


        if (
                recordatorioGuardar
                        .getKilometrosAnticipacion()
                        != null
        ) {

            recordatorio.setKilometrosAnticipacion(
                    recordatorioGuardar
                            .getKilometrosAnticipacion()
            );

        } else {

            recordatorio.setKilometrosAnticipacion(
                    new BigDecimal(
                            "500"
                    )
            );
        }


        recordatorio.setActivo(
                true
        );


        RecordatorioMantenimiento guardado =
                recordatorioRepository.save(
                        recordatorio
                );


        return modelMapper.map(
                guardado,
                RecordatorioMantenimientoSalida.class
        );
    }


    @Override
    public RecordatorioMantenimientoSalida editar(
            RecordatorioMantenimientoModificar dto
    ) {

        /*
         * El id es colocado desde el controlador
         * usando el valor de /{id}.
         */
        if (dto.getId() == null) {

            throw new IllegalArgumentException(
                    "El id del recordatorio es obligatorio"
            );
        }


        RecordatorioMantenimiento recordatorio =
                buscarEntidadPorId(
                        dto.getId()
                );


        /*
         * Evitamos que se asigne un
         * mantenimiento inexistente.
         */
        if (
                !mantenimientoRepository.existsById(
                        dto.getMantenimientoId()
                )
        ) {

            throw new IllegalArgumentException(
                    "El mantenimiento seleccionado no existe"
            );
        }


        recordatorio.setMantenimientoId(
                dto.getMantenimientoId()
        );


        recordatorio.setDiasAnticipacion(
                dto.getDiasAnticipacion()
        );


        recordatorio.setKilometrosAnticipacion(
                dto.getKilometrosAnticipacion()
        );


        /*
         * IMPORTANTE:
         *
         * Editar NO modifica activo.
         *
         * El estado activo/inactivo se
         * controla mediante el borrado lógico.
         */


        RecordatorioMantenimiento actualizado =
                recordatorioRepository.save(
                        recordatorio
                );


        return modelMapper.map(
                actualizado,
                RecordatorioMantenimientoSalida.class
        );
    }


    @Override
    public void eliminarPorId(
            Long id
    ) {

        RecordatorioMantenimiento recordatorio =
                buscarEntidadPorId(
                        id
                );


        /*
         * Borrado lógico.
         */
        recordatorio.setActivo(
                false
        );


        recordatorioRepository.save(
                recordatorio
        );
    }


    private RecordatorioMantenimiento buscarEntidadPorId(
            Long id
    ) {

        return recordatorioRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Recordatorio de mantenimiento no encontrado con id: "
                                                + id
                                )
                );
    }
}