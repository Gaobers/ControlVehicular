package org.esfe.modelos;

import jakarta.persistence.*;
import org.esfe.enums.EstadoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "vehiculo_id",
            nullable = false
    )
    private Long vehiculoId;


    @Column(
            name = "creado_por"
    )
    private Long creadoPor;


    @Column(
            name = "mantenimiento_origen_id"
    )
    private Long mantenimientoOrigenId;


    @Column(
            name = "servicio",
            nullable = false,
            length = 150
    )
    private String servicio;


    @Column(
            name = "observaciones",
            columnDefinition = "TEXT"
    )
    private String observaciones;


    @Column(
            name = "fecha_objetivo"
    )
    private LocalDate fechaObjetivo;


    @Column(
            name = "kilometraje_objetivo",
            precision = 12,
            scale = 1
    )
    private BigDecimal kilometrajeObjetivo;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false
    )
    private EstadoMantenimiento estado;


    @Column(
            name = "activo",
            nullable = false
    )
    private Boolean activo;


    @Column(
            name = "fecha_realizacion"
    )
    private LocalDate fechaRealizacion;


    @Column(
            name = "kilometraje_realizacion",
            precision = 12,
            scale = 1
    )
    private BigDecimal kilometrajeRealizacion;


    @Column(
            name = "fecha_creacion",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaCreacion;


    @Column(
            name = "fecha_actualizacion",
            insertable = false,
            updatable = false
    )
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