package org.esfe.dtos.gasto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class GastoSalidaDTO {

    private Long id;

    private Long vehiculoId;

    private Long categoriaId;

    private Long registradoPor;

    private BigDecimal monto;

    private String moneda;

    private LocalDate fecha;

    private String descripcion;

    private String numeroComprobante;

    private String proveedor;

    private Boolean activo;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaActualizacion;
}