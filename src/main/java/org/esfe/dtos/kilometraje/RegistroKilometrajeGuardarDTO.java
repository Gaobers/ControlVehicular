package org.esfe.dtos.kilometraje;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public class RegistroKilometrajeGuardarDTO {

    @NotNull(
            message = "El vehiculoId es obligatorio"
    )
    private Long vehiculoId;


    @NotNull(
            message = "El kilometraje es obligatorio"
    )
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El kilometraje no puede ser negativo"
    )
    private BigDecimal kilometraje;


    @Size(
            max = 255,
            message = "La observación no puede superar los 255 caracteres"
    )
    private String observacion;


    public RegistroKilometrajeGuardarDTO() {
    }


    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }


    public BigDecimal getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(BigDecimal kilometraje) {
        this.kilometraje = kilometraje;
    }


    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}