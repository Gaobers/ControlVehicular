package org.esfe.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta genérica de la API")
public class RespuestaDTO<T> {
    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private Boolean exito;
    
    @Schema(description = "Mensaje descriptivo del resultado", example = "Operación completada")
    private String mensaje;
    
    @Schema(description = "Datos de respuesta (tipo variable según endpoint)")
    private T datos;
}