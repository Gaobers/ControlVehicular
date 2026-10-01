package org.esfe.modelos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehiculos")
public class vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, unique = true)
    private String placa;

    private Double kilometrajeActual;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    private String documentacion;

    private LocalDate fechaVencimientoDocumentacion;

    @Column(nullable = false)
    private Long clienteId;

    public vehiculo() {}

    public vehiculo(String marca, String modelo, Integer anio, String placa, Double kilometrajeActual,
                    String caracteristicas, String documentacion, LocalDate fechaVencimientoDocumentacion, Long clienteId) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placa = placa;
        this.kilometrajeActual = kilometrajeActual;
        this.caracteristicas = caracteristicas;
        this.documentacion = documentacion;
        this.fechaVencimientoDocumentacion = fechaVencimientoDocumentacion;
        this.clienteId = clienteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public Double getKilometrajeActual() { return kilometrajeActual; }
    public void setKilometrajeActual(Double kilometrajeActual) { this.kilometrajeActual = kilometrajeActual; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public String getDocumentacion() { return documentacion; }
    public void setDocumentacion(String documentacion) { this.documentacion = documentacion; }

    public LocalDate getFechaVencimientoDocumentacion() { return fechaVencimientoDocumentacion; }
    public void setFechaVencimientoDocumentacion(LocalDate fechaVencimientoDocumentacion) { this.fechaVencimientoDocumentacion = fechaVencimientoDocumentacion; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
