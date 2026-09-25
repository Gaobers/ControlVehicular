package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "recordatorio_mantenimiento")
public class RecordatorioMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mantenimiento_id", nullable = false)
    private Long mantenimientoId;

    @Column(name = "dias_anticipacion")
    private Integer diasAnticipacion;

    @Column(name = "kilometros_anticipacion")
    private BigDecimal kilometrosAnticipacion;

    private Boolean activo = true;
}