package org.esfe.controladores;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.esfe.dtos.VehiculoRegistroRequest;
import org.esfe.dtos.VehiculoResponse;
import org.esfe.servicio.interfaces.vehiculoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
public class VehiculoController {

    private final vehiculoService vehiculoService;

    public VehiculoController(vehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> obtenerPorId(
            @PathVariable Long id) {

        VehiculoResponse response = vehiculoService.obtenerPorId(id);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> actualizarVehiculo(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoRegistroRequest request,
            @RequestHeader("X-Usuario-Id") Long usuarioAutenticadoId,
            @RequestHeader("X-Usuario-Rol") String rolUsuario) {

        VehiculoResponse response = vehiculoService.actualizarVehiculo(
                id,
                request,
                usuarioAutenticadoId,
                rolUsuario
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long usuarioAutenticadoId,
            @RequestHeader("X-Usuario-Rol") String rolUsuario) {

        vehiculoService.eliminarVehiculo(
                id,
                usuarioAutenticadoId,
                rolUsuario
        );

        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listarVehiculos() {

        List<VehiculoResponse> lista = vehiculoService.listarTodos();

        return ResponseEntity.ok(lista);
    }


    @PostMapping
    public ResponseEntity<VehiculoResponse> registrarVehiculo(
            @Valid @RequestBody VehiculoRegistroRequest request,
            @RequestHeader("X-Usuario-Id") Long usuarioAutenticadoId,
            @RequestHeader("X-Usuario-Rol") String rolUsuario) {

        VehiculoResponse response = vehiculoService.registrarVehiculo(
                request,
                usuarioAutenticadoId,
                rolUsuario
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
