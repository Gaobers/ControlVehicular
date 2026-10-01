package org.esfe.dtos.Vehiculo;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VehiculoGuardarDTO {

    @NotNull(message = "El propietario es obligatorio")
    private Long propietarioId;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 80, message = "La marca no puede superar 80 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede superar 100 caracteres")
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año mínimo permitido es 1900")
    @Max(value = 2200, message = "El año máximo permitido es 2200")
    private Short anio;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20, message = "La placa no puede superar 20 caracteres")
    private String placa;

    @Size(max = 60, message = "El color no puede superar 60 caracteres")
    private String color;

    @NotNull(message = "El kilometraje actual es obligatorio")
    @DecimalMin(
            value = "0.0",
            message = "El kilometraje debe ser mayor o igual a cero"
    )
    private BigDecimal kilometrajeActual;

    @Size(max = 50, message = "El VIN no puede superar 50 caracteres")
    private String vin;

    @Size(max = 150, message = "El motor no puede superar 150 caracteres")
    private String motor;
}