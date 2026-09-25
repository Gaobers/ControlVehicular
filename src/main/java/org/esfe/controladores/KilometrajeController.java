package org.esfe.controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.esfe.dtos.HistorialKilometrajeRequest;
import org.esfe.dtos.RespuestaDTO;
import org.esfe.modelos.HistorialKilometraje;
import org.esfe.servicio.implementaciones.KilometrajeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kilometrajes")
@CrossOrigin(origins = "*")
@Tag(name = "Kilometrajes", description = "Gestión del historial de kilometraje de vehículos")
public class KilometrajeController {

    private final KilometrajeService kilometrajeService;

    public KilometrajeController(KilometrajeService kilometrajeService) {
        this.kilometrajeService = kilometrajeService;
    }

    @Operation(
            summary = "Registrar nuevo kilometraje",
            description = "Guarda una nueva lectura de kilometraje validando que no sea inferior al último registrado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kilometraje registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Kilometraje inferior al último registrado / Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<RespuestaDTO<HistorialKilometraje>> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo registro de kilometraje", required = true)
            @RequestBody HistorialKilometrajeRequest request) {

        if (request.getVehiculoId() == null || request.getKilometraje() == null) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaDTO<>(false, "vehiculoId y kilometraje son requeridos", null));
        }

        HistorialKilometraje entidad = new HistorialKilometraje();
        entidad.setVehiculoId(request.getVehiculoId());
        entidad.setKilometraje(request.getKilometraje());
        entidad.setObservaciones(request.getObservaciones());

        RespuestaDTO<HistorialKilometraje> respuesta = kilometrajeService.registrar(entidad);

        if (!respuesta.getExito()) {
            return ResponseEntity.badRequest().body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Obtener historial de kilometraje por vehículo",
            description = "Devuelve la lista completa de lecturas ordenadas por fecha descendente (más reciente primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<RespuestaDTO<List<HistorialKilometraje>>> obtenerHistorial(
            @Parameter(name = "vehiculoId", description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long vehiculoId) {
        RespuestaDTO<List<HistorialKilometraje>> respuesta = kilometrajeService.obtenerHistorial(vehiculoId);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Obtener kilometraje actual (más alto) de un vehículo",
            description = "Devuelve el registro con el kilometraje más alto registrado para el vehículo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kilometraje actual encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró kilometraje para el vehículo"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/vehiculo/{vehiculoId}/actual")
    public ResponseEntity<RespuestaDTO<HistorialKilometraje>> obtenerActual(
            @Parameter(name = "vehiculoId", description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long vehiculoId) {
        RespuestaDTO<HistorialKilometraje> respuesta = kilometrajeService.obtenerActual(vehiculoId);

        if (!respuesta.getExito()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Obtener registro de kilometraje por ID",
            description = "Busca un registro específico de kilometraje por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaDTO<HistorialKilometraje>> obtenerPorId(
            @Parameter(name = "id", description = "ID del registro de kilometraje", required = true, example = "1")
            @PathVariable Long id) {
        RespuestaDTO<HistorialKilometraje> respuesta = kilometrajeService.obtenerPorId(id);

        if (!respuesta.getExito()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Actualizar registro de kilometraje",
            description = "Actualiza un registro existente validando que el nuevo valor no sea inferior al actual"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kilometraje actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Nuevo kilometraje inferior al actual / Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaDTO<HistorialKilometraje>> actualizar(
            @Parameter(name = "id", description = "ID del registro de kilometraje", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del kilometraje", required = true)
            @RequestBody HistorialKilometrajeRequest request) {

        if (request.getKilometraje() == null) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaDTO<>(false, "El kilometraje es requerido", null));
        }

        HistorialKilometraje entidad = new HistorialKilometraje();
        entidad.setKilometraje(request.getKilometraje());
        entidad.setObservaciones(request.getObservaciones());

        RespuestaDTO<HistorialKilometraje> respuesta = kilometrajeService.actualizar(id, entidad);

        if (!respuesta.getExito()) {
            String mensaje = respuesta.getMensaje();
            HttpStatus status = mensaje.contains("no encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Eliminar registro de kilometraje",
            description = "Elimina permanentemente un registro de kilometraje por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RespuestaDTO<Void>> eliminar(
            @Parameter(name = "id", description = "ID del registro de kilometraje", required = true, example = "1")
            @PathVariable Long id) {
        RespuestaDTO<Void> respuesta = kilometrajeService.eliminar(id);

        if (!respuesta.getExito()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }
}