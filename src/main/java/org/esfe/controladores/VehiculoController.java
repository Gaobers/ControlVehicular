package org.esfe.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.esfe.dtos.KilometrajeRequest;
import org.esfe.dtos.RespuestaDTO;
import org.esfe.modelos.Vehiculo;
import org.esfe.servicio.implementaciones.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
@Tag(name = "Vehículos", description = "Gestión de vehículos y kilometraje (CV-19)")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Operation(
        summary = "Listar vehículos por usuario",
        description = "Obtiene la lista de vehículos asociados a un usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículos encontrados"),
        @ApiResponse(responseCode = "400", description = "Parámetro inválido")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<RespuestaDTO<Collection<Vehiculo>>> obtenerPorUsuario(
            @Parameter(name = "usuarioId", description = "ID del usuario propietario", required = true, example = "100")
            @PathVariable Long usuarioId) {
        Collection<Vehiculo> vehiculos = vehiculoService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(new RespuestaDTO<>(true, "Vehículos encontrados", vehiculos));
    }

    @Operation(
        summary = "Actualizar kilometraje",
        description = "Actualiza el kilometraje actual de un vehículo. El nuevo valor debe ser ≥ al actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kilometraje actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Nuevo kilometraje inferior al actual / Kilometraje requerido"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @PatchMapping("/{id}/kilometraje")
    public ResponseEntity<RespuestaDTO<Vehiculo>> actualizarKilometraje(
            @Parameter(name = "id", description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevo kilometraje", required = true)
            @RequestBody KilometrajeRequest request) {
        
        if (request.getKilometraje() == null) {
            return ResponseEntity.badRequest()
                .body(new RespuestaDTO<>(false, "El kilometraje es requerido", null));
        }

        RespuestaDTO<Vehiculo> respuesta = vehiculoService.actualizarKilometraje(id, request.getKilometraje());
        
        if (!respuesta.getExito()) {
            String mensaje = respuesta.getMensaje();
            HttpStatus status = mensaje.contains("no encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(respuesta);
        }

        return ResponseEntity.ok(respuesta);
    }
}