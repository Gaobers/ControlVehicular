package org.esfe.dtos.Vehiculo;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.esfe.enums.EstadoVehiculo;

import java.math.BigDecimal;

@Getter
@Setter
public class VehiculoModificarDTO {

    private Long id;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 80)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año mínimo permitido es 1900")
    @Max(value = 2200, message = "El año máximo permitido es 2200")
    private Short anio;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20)
    private String placa;

    @Size(max = 60)
    private String color;

    @NotNull(message = "El kilometraje actual es obligatorio")
    @DecimalMin(value = "0.0")
    private BigDecimal kilometrajeActual;

    @Size(max = 50)
    private String vin;

    @Size(max = 150)
    private String motor;

    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;
}