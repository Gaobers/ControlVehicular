package org.esfe.dtos.RecordatorioMantenimiento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public class RecordatorioMantenimientoModificar {

    private Long id;


    @NotNull(
            message = "El mantenimientoId es obligatorio"
    )
    private Long mantenimientoId;


    @NotNull(
            message = "Los días de anticipación son obligatorios"
    )
    @Min(
            value = 0,
            message = "Los días de anticipación no pueden ser negativos"
    )
    private Integer diasAnticipacion;


    @NotNull(
            message = "Los kilómetros de anticipación son obligatorios"
    )
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Los kilómetros de anticipación no pueden ser negativos"
    )
    private BigDecimal kilometrosAnticipacion;


    public RecordatorioMantenimientoModificar() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getMantenimientoId() {
        return mantenimientoId;
    }

    public void setMantenimientoId(
            Long mantenimientoId
    ) {
        this.mantenimientoId =
                mantenimientoId;
    }


    public Integer getDiasAnticipacion() {
        return diasAnticipacion;
    }

    public void setDiasAnticipacion(
            Integer diasAnticipacion
    ) {
        this.diasAnticipacion =
                diasAnticipacion;
    }


    public BigDecimal getKilometrosAnticipacion() {
        return kilometrosAnticipacion;
    }

    public void setKilometrosAnticipacion(
            BigDecimal kilometrosAnticipacion
    ) {
        this.kilometrosAnticipacion =
                kilometrosAnticipacion;
    }
}