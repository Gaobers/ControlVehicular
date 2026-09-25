package org.esfe.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Respuesta genérica de la API")
public class RespuestaDTO<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private Boolean exito;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Operación completada")
    private String mensaje;

    @Schema(description = "Datos de respuesta (tipo variable según endpoint)")
    private T datos;

    public RespuestaDTO(Boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }
}