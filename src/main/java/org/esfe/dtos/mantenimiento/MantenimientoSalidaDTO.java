package org.esfe.dtos.mantenimiento;

import org.esfe.enums.EstadoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MantenimientoSalidaDTO {

    private Long id;

    private Long vehiculoId;

    private Long creadoPor;

    private Long mantenimientoOrigenId;

    private String servicio;

    private String observaciones;

    private LocalDate fechaObjetivo;

    private BigDecimal kilometrajeObjetivo;

    private EstadoMantenimiento estado;

    private Boolean activo;

    private LocalDate fechaRealizacion;

    private BigDecimal kilometrajeRealizacion;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;


    public MantenimientoSalidaDTO() {
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

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }


    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
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


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }


    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion
    ) {
        this.fechaActualizacion =
                fechaActualizacion;
    }
}