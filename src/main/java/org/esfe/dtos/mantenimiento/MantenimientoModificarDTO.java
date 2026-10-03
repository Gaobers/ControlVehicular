package org.esfe.dtos.mantenimiento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.esfe.enums.EstadoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;


public class MantenimientoModificarDTO {

    private Long id;


    @NotNull(
            message = "El vehiculoId es obligatorio"
    )
    private Long vehiculoId;


    @NotBlank(
            message = "El servicio es obligatorio"
    )
    private String servicio;


    @NotNull(
            message = "El estado es obligatorio"
    )
    private EstadoMantenimiento estado;


    private LocalDate fechaObjetivo;


    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El kilometraje objetivo no puede ser negativo"
    )
    private BigDecimal kilometrajeObjetivo;


    public MantenimientoModificarDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(
            Long vehiculoId
    ) {
        this.vehiculoId =
                vehiculoId;
    }


    public String getServicio() {
        return servicio;
    }

    public void setServicio(
            String servicio
    ) {
        this.servicio =
                servicio;
    }


    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoMantenimiento estado
    ) {
        this.estado =
                estado;
    }


    public LocalDate getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(
            LocalDate fechaObjetivo
    ) {
        this.fechaObjetivo =
                fechaObjetivo;
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
}