package org.esfe.modelos;

import jakarta.persistence.*;
import org.esfe.enums.EstadoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehiculoId;
    private Long creadoPor;
    private Long mantenimientoOrigenId;

    private String servicio;
    private String observaciones;

    private LocalDate fechaObjetivo;
    private BigDecimal kilometrajeObjetivo;

    @Enumerated(EnumType.STRING)
    private EstadoMantenimiento estado;

    private LocalDate fechaRealizacion;
    private BigDecimal kilometrajeRealizacion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Mantenimiento() {
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

    public void setMantenimientoOrigenId(Long mantenimientoOrigenId) {
        this.mantenimientoOrigenId = mantenimientoOrigenId;
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

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(LocalDate fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public BigDecimal getKilometrajeObjetivo() {
        return kilometrajeObjetivo;
    }

    public void setKilometrajeObjetivo(BigDecimal kilometrajeObjetivo) {
        this.kilometrajeObjetivo = kilometrajeObjetivo;
    }

    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMantenimiento estado) {
        this.estado = estado;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public BigDecimal getKilometrajeRealizacion() {
        return kilometrajeRealizacion;
    }

    public void setKilometrajeRealizacion(BigDecimal kilometrajeRealizacion) {
        this.kilometrajeRealizacion = kilometrajeRealizacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}