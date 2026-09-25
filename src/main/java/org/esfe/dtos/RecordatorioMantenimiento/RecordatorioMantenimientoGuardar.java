package org.esfe.dtos.RecordatorioMantenimiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecordatorioMantenimientoGuardar implements Serializable {

    private Long mantenimientoId;

    private Integer diasAnticipacion;

    private BigDecimal kilometrosAnticipacion;
}