package org.esfe.dtos.Vehiculo;

import lombok.Getter;
import lombok.Setter;
import org.esfe.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VehiculoSalidaDTO {

    private Long id;

    private Long propietarioId;

    private String marca;

    private String modelo;

    private Short anio;

    private String placa;

    private String color;

    private BigDecimal kilometrajeActual;

    private String vin;

    private String motor;

    private EstadoVehiculo estado;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaActualizacion;
}