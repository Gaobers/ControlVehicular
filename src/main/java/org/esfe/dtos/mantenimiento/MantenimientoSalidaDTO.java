package org.esfe.dtos.mantenimiento;

import org.esfe.enums.EstadoMantenimiento;

public class MantenimientoSalidaDTO {

    private Long id;
    private Long clienteId;
    private Long vehiculoId;
    private String vehiculoInfo;
    private String servicio;
    private String categoria;
    private EstadoMantenimiento estado;

    public MantenimientoSalidaDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public String getVehiculoInfo() { return vehiculoInfo; }
    public void setVehiculoInfo(String vehiculoInfo) { this.vehiculoInfo = vehiculoInfo; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public EstadoMantenimiento getEstado() { return estado; }
    public void setEstado(EstadoMantenimiento estado) { this.estado = estado; }
}

