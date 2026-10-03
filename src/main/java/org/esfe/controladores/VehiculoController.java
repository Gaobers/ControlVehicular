package org.esfe.controladores;

import jakarta.validation.Valid;

import org.esfe.dtos.Vehiculo.VehiculoGuardarDTO;
import org.esfe.dtos.Vehiculo.VehiculoModificarDTO;
import org.esfe.dtos.Vehiculo.VehiculoSalidaDTO;
import org.esfe.servicio.interfaces.IVehiculoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final IVehiculoService vehiculoService;

    public VehiculoController(
            IVehiculoService vehiculoService
    ) {
        this.vehiculoService = vehiculoService;
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping
    public ResponseEntity<Page<VehiculoSalidaDTO>>
    mostrarTodosPaginados(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                vehiculoService
                        .obtenerTodosPaginados(pageable)
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping("/lista")
    public ResponseEntity<List<VehiculoSalidaDTO>>
    mostrarTodos() {

        return ResponseEntity.ok(
                vehiculoService.obtenerTodos()
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoSalidaDTO>
    buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                vehiculoService.obtenerPorId(id)
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PostMapping
    public ResponseEntity<VehiculoSalidaDTO>
    crear(
            @Valid
            @RequestBody
            VehiculoGuardarDTO vehiculoGuardar
    ) {

        VehiculoSalidaDTO vehiculo =
                vehiculoService.crear(
                        vehiculoGuardar
                );

        return ResponseEntity
                .status(201)
                .body(vehiculo);
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoSalidaDTO>
    editar(
            @PathVariable Long id,

            @Valid
            @RequestBody
            VehiculoModificarDTO vehiculoModificar
    ) {

        vehiculoModificar.setId(id);

        return ResponseEntity.ok(
                vehiculoService.editar(
                        vehiculoModificar
                )
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    eliminar(
            @PathVariable Long id
    ) {

        vehiculoService.eliminarPorId(id);

        return ResponseEntity.ok(
                "Vehículo archivado correctamente"
        );
    }
}