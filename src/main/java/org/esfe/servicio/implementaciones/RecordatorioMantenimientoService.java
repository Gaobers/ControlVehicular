package org.esfe.servicio.implementaciones;

import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoGuardar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoModificar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoSalida;
import org.esfe.modelos.RecordatorioMantenimiento;
import org.esfe.repositorios.IRecordatorioMantenimientoRepository;
import org.esfe.servicio.interfaces.IRecordatorioMantenimientoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordatorioMantenimientoService
        implements IRecordatorioMantenimientoService {

    @Autowired
    private IRecordatorioMantenimientoRepository recordatorioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RecordatorioMantenimientoSalida> obtenerTodos() {

        List<RecordatorioMantenimiento> recordatorios =
                recordatorioRepository.findAll();

        return recordatorios.stream()
                .map(recordatorio ->
                        modelMapper.map(
                                recordatorio,
                                RecordatorioMantenimientoSalida.class
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public Page<RecordatorioMantenimientoSalida> obtenerTodosPaginados(
            Pageable pageable) {

        Page<RecordatorioMantenimiento> page =
                recordatorioRepository.findAll(pageable);

        List<RecordatorioMantenimientoSalida> recordatoriosDto =
                page.stream()
                        .map(recordatorio ->
                                modelMapper.map(
                                        recordatorio,
                                        RecordatorioMantenimientoSalida.class
                                )
                        )
                        .collect(Collectors.toList());

        return new PageImpl<>(
                recordatoriosDto,
                page.getPageable(),
                page.getTotalElements()
        );
    }

    @Override
    public RecordatorioMantenimientoSalida obtenerPorId(Long id) {

        RecordatorioMantenimiento recordatorio =
                recordatorioRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recordatorio de mantenimiento no encontrado"
                                )
                        );

        return modelMapper.map(
                recordatorio,
                RecordatorioMantenimientoSalida.class
        );
    }

    @Override
    public RecordatorioMantenimientoSalida crear(
            RecordatorioMantenimientoGuardar recordatorioGuardar) {

        RecordatorioMantenimiento recordatorio =
                new RecordatorioMantenimiento();

        recordatorio.setMantenimientoId(
                recordatorioGuardar.getMantenimientoId()
        );

        if (recordatorioGuardar.getDiasAnticipacion() != null) {
            recordatorio.setDiasAnticipacion(
                    recordatorioGuardar.getDiasAnticipacion()
            );
        } else {
            recordatorio.setDiasAnticipacion(15);
        }

        if (recordatorioGuardar.getKilometrosAnticipacion() != null) {
            recordatorio.setKilometrosAnticipacion(
                    recordatorioGuardar.getKilometrosAnticipacion()
            );
        } else {
            recordatorio.setKilometrosAnticipacion(
                    new BigDecimal("500")
            );
        }

        recordatorio.setActivo(true);

        RecordatorioMantenimiento guardado =
                recordatorioRepository.save(recordatorio);

        return modelMapper.map(
                guardado,
                RecordatorioMantenimientoSalida.class
        );
    }

    @Override
    public RecordatorioMantenimientoSalida editar(
            RecordatorioMantenimientoModificar recordatorioModificar) {

        RecordatorioMantenimiento recordatorio =
                recordatorioRepository
                        .findById(recordatorioModificar.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recordatorio de mantenimiento no encontrado"
                                )
                        );

        recordatorio.setDiasAnticipacion(
                recordatorioModificar.getDiasAnticipacion()
        );

        recordatorio.setKilometrosAnticipacion(
                recordatorioModificar.getKilometrosAnticipacion()
        );

        recordatorio.setActivo(
                recordatorioModificar.getActivo()
        );

        RecordatorioMantenimiento actualizado =
                recordatorioRepository.save(recordatorio);

        return modelMapper.map(
                actualizado,
                RecordatorioMantenimientoSalida.class
        );
    }

    @Override
    public void eliminarPorId(Long id) {

        RecordatorioMantenimiento recordatorio =
                recordatorioRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recordatorio de mantenimiento no encontrado"
                                )
                        );

        recordatorio.setActivo(false);

        recordatorioRepository.save(recordatorio);
    }
}