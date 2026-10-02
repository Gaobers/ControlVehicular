package org.esfe.controladores;

import jakarta.validation.Valid;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;

import org.esfe.enums.EstadoMantenimiento;

import org.esfe.servicio.interfaces.IMantenimientoService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {


    @Autowired
    private IMantenimientoService mantenimientoService;


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping
    public ResponseEntity<Page<MantenimientoSalidaDTO>>
    mostrarTodosPaginados(

            @RequestParam(required = false)
            Long vehiculoId,

            @RequestParam(required = false)
            EstadoMantenimiento estado,

            @RequestParam(required = false)
            Boolean activo,

            Pageable pageable
    ) {

        Page<MantenimientoSalidaDTO> mantenimientos =
                mantenimientoService
                        .obtenerTodosPaginados(
                                vehiculoId,
                                estado,
                                activo,
                                pageable
                        );


        if (mantenimientos.hasContent()) {

            return ResponseEntity.ok(
                    mantenimientos
            );
        }


        return ResponseEntity
                .notFound()
                .build();
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping("/lista")
    public ResponseEntity<List<MantenimientoSalidaDTO>>
    mostrarTodos(

            @RequestParam(required = false)
            Long vehiculoId,

            @RequestParam(required = false)
            EstadoMantenimiento estado,

            @RequestParam(required = false)
            Boolean activo
    ) {

        List<MantenimientoSalidaDTO> mantenimientos =
                mantenimientoService
                        .obtenerTodos(
                                vehiculoId,
                                estado,
                                activo
                        );


        if (!mantenimientos.isEmpty()) {

            return ResponseEntity.ok(
                    mantenimientos
            );
        }


        return ResponseEntity
                .notFound()
                .build();
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoSalidaDTO>
    buscarPorId(
            @PathVariable Long id
    ) {

        MantenimientoSalidaDTO mantenimiento =
                mantenimientoService
                        .obtenerPorId(id);


        if (mantenimiento != null) {

            return ResponseEntity.ok(
                    mantenimiento
            );
        }


        return ResponseEntity
                .notFound()
                .build();
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PostMapping
    public ResponseEntity<MantenimientoSalidaDTO>
    crear(

            @Valid
            @RequestBody
            MantenimientoGuardarDTO mantenimientoGuardar
    ) {

        MantenimientoSalidaDTO mantenimiento =
                mantenimientoService.crear(
                        mantenimientoGuardar
                );


        return ResponseEntity.ok(
                mantenimiento
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoSalidaDTO>
    editar(

            @PathVariable Long id,

            @Valid
            @RequestBody
            MantenimientoModificarDTO mantenimientoModificar
    ) {

        mantenimientoModificar.setId(
                id
        );


        MantenimientoSalidaDTO mantenimiento =
                mantenimientoService.editar(
                        mantenimientoModificar
                );


        return ResponseEntity.ok(
                mantenimiento
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

        mantenimientoService.eliminarPorId(
                id
        );


        return ResponseEntity.ok(
                "Mantenimiento desactivado correctamente"
        );
    }
}