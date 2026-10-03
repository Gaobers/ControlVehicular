package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto")
@Getter
@Setter
@NoArgsConstructor
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(
            name = "vehiculo_id",
            nullable = false
    )
    private Long vehiculoId;


    @Column(
            name = "categoria_id",
            nullable = false
    )
    private Long categoriaId;


    @Column(
            name = "registrado_por"
    )
    private Long registradoPor;


    @Column(
            name = "monto",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal monto;


    @Column(
            name = "moneda",
            nullable = false,
            columnDefinition = "CHAR(3)"
    )
    private String moneda;


    @Column(
            name = "fecha",
            nullable = false
    )
    private LocalDate fecha;


    @Column(
            name = "descripcion",
            columnDefinition = "TEXT"
    )
    private String descripcion;


    @Column(
            name = "numero_comprobante",
            length = 100
    )
    private String numeroComprobante;


    @Column(
            name = "proveedor",
            length = 150
    )
    private String proveedor;


    @Column(
            name = "activo",
            nullable = false
    )
    private Boolean activo;


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