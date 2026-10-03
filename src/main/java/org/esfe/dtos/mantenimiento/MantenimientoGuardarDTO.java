package org.esfe.dtos.mantenimiento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.esfe.enums.EstadoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MantenimientoGuardarDTO {

    @NotNull(
            message = "El vehiculoId es obligatorio"
    )
    private Long vehiculoId;


    private Long mantenimientoOrigenId;


    @NotBlank(
            message = "El servicio es obligatorio"
    )
    @Size(
            max = 150,
            message = "El servicio no puede superar los 150 caracteres"
    )
    private String servicio;


    private String observaciones;


    private LocalDate fechaObjetivo;


    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El kilometraje objetivo no puede ser negativo"
    )
    private BigDecimal kilometrajeObjetivo;


    @NotNull(
            message = "El estado es obligatorio"
    )
    private EstadoMantenimiento estado;


    private LocalDate fechaRealizacion;


    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El kilometraje de realización no puede ser negativo"
    )
    private BigDecimal kilometrajeRealizacion;


    public MantenimientoGuardarDTO() {
    }


    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }


    public Long getMantenimientoOrigenId() {
        return mantenimientoOrigenId;
    }

    public void setMantenimientoOrigenId(
            Long mantenimientoOrigenId
    ) {
        this.mantenimientoOrigenId =
                mantenimientoOrigenId;
    }


    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }


    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones
    ) {
        this.observaciones = observaciones;
    }


    public LocalDate getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(
            LocalDate fechaObjetivo
    ) {
        this.fechaObjetivo = fechaObjetivo;
    }


    public BigDecimal getKilometrajeObjetivo() {
        return kilometrajeObjetivo;
    }

    public void setKilometrajeObjetivo(
            BigDecimal kilometrajeObjetivo
    ) {
        this.kilometrajeObjetivo =
                kilometrajeObjetivo;
    }


    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoMantenimiento estado
    ) {
        this.estado = estado;
    }


    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(
            LocalDate fechaRealizacion
    ) {
        this.fechaRealizacion =
                fechaRealizacion;
    }


    public BigDecimal getKilometrajeRealizacion() {
        return kilometrajeRealizacion;
    }

    public void setKilometrajeRealizacion(
            BigDecimal kilometrajeRealizacion
    ) {
        this.kilometrajeRealizacion =
                kilometrajeRealizacion;
    }
}