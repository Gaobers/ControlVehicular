package org.esfe.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public class VehiculoRegistroRequest {

    @NotBlank(message = "La marca es un dato obligatorio")
    private String marca;

    @NotBlank(message = "El modelo es un dato obligatorio")
    private String modelo;

    @NotNull(message = "El año es un dato obligatorio")
    private Integer anio;

    @NotBlank(message = "La placa es un dato obligatorio")
    private String placa;

    @PositiveOrZero(message = "El kilometraje debe ser mayor o igual a cero")
    private Double kilometrajeActual;

    private String caracteristicas;
    private String documentacion;
    private LocalDate fechaVencimientoDocumentacion;

    // Obligatorio únicamente cuando el registro lo realiza un Administrador
    private Long clienteId;

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

