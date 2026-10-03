package org.esfe.dtos.gasto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GastoModificarDTO {

    private Long id;


    @NotNull(
            message = "El ID del vehículo es obligatorio."
    )
    private Long vehiculoId;


    @NotNull(
            message = "El ID de la categoría es obligatorio."
    )
    private Long categoriaId;


    @NotNull(
            message = "El monto es obligatorio."
    )
    @DecimalMin(
            value = "0.01",
            inclusive = true,
            message = "El monto debe ser mayor a cero."
    )
    private BigDecimal monto;


    @Pattern(
            regexp = "^[A-Za-z]{3}$",
            message = "La moneda debe contener exactamente 3 letras."
    )
    private String moneda;


    @NotNull(
            message = "La fecha es obligatoria."
    )
    private LocalDate fecha;


    private String descripcion;


    @Size(
            max = 100,
            message = "El número de comprobante no puede superar los 100 caracteres."
    )
    private String numeroComprobante;


    @Size(
            max = 150,
            message = "El proveedor no puede superar los 150 caracteres."
    )
    private String proveedor;


    @NotNull(
            message = "El estado del gasto es obligatorio."
    )
    private Boolean activo;
}