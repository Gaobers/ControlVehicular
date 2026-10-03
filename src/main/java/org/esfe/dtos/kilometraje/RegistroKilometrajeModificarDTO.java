package org.esfe.dtos.kilometraje;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public class RegistroKilometrajeModificarDTO {

    @NotNull(
            message = "El id es obligatorio"
    )
    private Long id;


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


    @NotNull(
            message = "El estado activo es obligatorio"
    )
    private Boolean activo;


    public RegistroKilometrajeModificarDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}