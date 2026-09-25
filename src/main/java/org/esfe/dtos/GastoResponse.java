package org.esfe.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GastoResponse {

    private Long id;
    private Long vehiculoId;
    private String categoria;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
}