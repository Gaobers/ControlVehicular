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
public class RecordatorioMantenimientoModificar implements Serializable {

    private Long id;

    private Integer diasAnticipacion;

    private BigDecimal kilometrosAnticipacion;

    private Boolean activo;
}