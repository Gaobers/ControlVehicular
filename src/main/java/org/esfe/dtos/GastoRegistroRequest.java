package org.esfe.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class GastoRegistroRequest {

    @NotNull(message = "El ID del vehiculo es obligatorio.")
    private Long vehiculoId;

    @NotBlank(message = "La categoria del gasto es obligatoria.")
    private String categoria;

    @NotNull(message = "El monto del gasto es obligatorio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser un valor positivo mayor o igual a 0.01")
    private BigDecimal monto;

    @NotNull(message = "La fecha del gasto es obligatoria.")
    private LocalDate fecha;

    private String descripcion;
}