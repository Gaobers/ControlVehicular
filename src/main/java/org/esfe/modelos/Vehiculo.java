package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esfe.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "propietario_id", nullable = false)
    private Long propietarioId;

    @Column(name = "marca", nullable = false, length = 80)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "anio", nullable = false)
    private Short anio;

    @Column(name = "placa", nullable = false, unique = true, length = 20)
    private String placa;

    @Column(name = "color", length = 60)
    private String color;

    @Column(
            name = "kilometraje_actual",
            nullable = false,
            precision = 12,
            scale = 1
    )
    private BigDecimal kilometrajeActual;

    @Column(name = "vin", unique = true, length = 50)
    private String vin;

    @Column(name = "motor", length = 150)
    private String motor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVehiculo estado;

    @Column(
            name = "fecha_registro",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaRegistro;

    @Column(
            name = "fecha_actualizacion",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaActualizacion;
}