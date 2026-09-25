package org.esfe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialKilometrajeRequest {
    private Long vehiculoId;
    private Integer kilometraje;
    private String observaciones;
}