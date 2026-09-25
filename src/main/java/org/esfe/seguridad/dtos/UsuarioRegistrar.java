package org.esfe.seguridad.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistrar {

    private String nombreCompleto;
    private String telefono;
    private String correo;
    private String clave;
}