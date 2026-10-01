package org.esfe.dtos.RecordatorioMantenimiento;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class RecordatorioMantenimientoSalida implements Serializable {

    private Long id;

    private Long mantenimientoId;

    private Integer diasAnticipacion;

    private BigDecimal kilometrosAnticipacion;

    private Boolean activo;
}