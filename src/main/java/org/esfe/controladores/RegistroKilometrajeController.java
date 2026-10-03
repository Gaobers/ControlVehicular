package org.esfe.controladores;

import jakarta.validation.Valid;

import org.esfe.dtos.kilometraje.RegistroKilometrajeGuardarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeModificarDTO;
import org.esfe.dtos.kilometraje.RegistroKilometrajeSalidaDTO;

import org.esfe.servicio.interfaces.IRegistroKilometrajeService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/kilometrajes")
public class RegistroKilometrajeController {


    @Autowired
    private IRegistroKilometrajeService
            registroKilometrajeService;


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping
    public ResponseEntity<Page<RegistroKilometrajeSalidaDTO>>
    mostrarTodosPaginados(

            @RequestParam(required = false)
            Long vehiculoId,

            @RequestParam(required = false)
            Boolean activo,

            Pageable pageable
    ) {

        Page<RegistroKilometrajeSalidaDTO> registros =
                registroKilometrajeService
                        .obtenerTodosPaginados(
                                vehiculoId,
                                activo,
                                pageable
                        );


        if (registros.hasContent()) {

            return ResponseEntity.ok(
                    registros
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
    public ResponseEntity<List<RegistroKilometrajeSalidaDTO>>
    mostrarTodos(

            @RequestParam(required = false)
            Long vehiculoId,

            @RequestParam(required = false)
            Boolean activo
    ) {

        List<RegistroKilometrajeSalidaDTO> registros =
                registroKilometrajeService
                        .obtenerTodos(
                                vehiculoId,
                                activo
                        );


        if (!registros.isEmpty()) {

            return ResponseEntity.ok(
                    registros
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
    public ResponseEntity<RegistroKilometrajeSalidaDTO>
    buscarPorId(
            @PathVariable Long id
    ) {

        RegistroKilometrajeSalidaDTO registro =
                registroKilometrajeService
                        .obtenerPorId(id);


        if (registro != null) {

            return ResponseEntity.ok(
                    registro
            );
        }


        return ResponseEntity
                .notFound()
                .build();
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @GetMapping("/vehiculo/{vehiculoId}/actual")
    public ResponseEntity<RegistroKilometrajeSalidaDTO>
    obtenerActual(
            @PathVariable Long vehiculoId
    ) {

        RegistroKilometrajeSalidaDTO registro =
                registroKilometrajeService
                        .obtenerActual(
                                vehiculoId
                        );


        return ResponseEntity.ok(
                registro
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PostMapping
    public ResponseEntity<RegistroKilometrajeSalidaDTO>
    crear(

            @Valid
            @RequestBody
            RegistroKilometrajeGuardarDTO registroGuardar
    ) {

        RegistroKilometrajeSalidaDTO registro =
                registroKilometrajeService
                        .crear(
                                registroGuardar
                        );


        return ResponseEntity.ok(
                registro
        );
    }


    @PreAuthorize(
            "hasAnyRole('CLIENTE', 'ADMINISTRADOR')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<RegistroKilometrajeSalidaDTO>
    editar(

            @PathVariable Long id,

            @Valid
            @RequestBody
            RegistroKilometrajeModificarDTO registroModificar
    ) {

        registroModificar.setId(
                id
        );


        RegistroKilometrajeSalidaDTO registro =
                registroKilometrajeService
                        .editar(
                                registroModificar
                        );


        return ResponseEntity.ok(
                registro
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

        registroKilometrajeService
                .eliminarPorId(
                        id
                );


        return ResponseEntity.ok(
                "Registro de kilometraje desactivado correctamente"
        );
    }
}