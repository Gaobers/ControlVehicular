package org.esfe.controladores;

import jakarta.validation.Valid;

import org.esfe.dtos.gasto.GastoGuardarDTO;
import org.esfe.dtos.gasto.GastoModificarDTO;
import org.esfe.dtos.gasto.GastoSalidaDTO;
import org.esfe.servicio.interfaces.IGastoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    @Autowired
    private IGastoService gastoService;


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<GastoSalidaDTO>> mostrarTodosPaginados(
            Pageable pageable) {

        Page<GastoSalidaDTO> gastos =
                gastoService.obtenerTodosPaginados(pageable);

        if (gastos.hasContent()) {
            return ResponseEntity.ok(gastos);
        }

        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping("/lista")
    public ResponseEntity<List<GastoSalidaDTO>> mostrarTodos() {

        List<GastoSalidaDTO> gastos =
                gastoService.obtenerTodos();

        if (!gastos.isEmpty()) {
            return ResponseEntity.ok(gastos);
        }

        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<GastoSalidaDTO> buscarPorId(
            @PathVariable Long id) {

        GastoSalidaDTO gasto =
                gastoService.obtenerPorId(id);

        if (gasto != null) {
            return ResponseEntity.ok(gasto);
        }

        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<GastoSalidaDTO> crear(
            @Valid
            @RequestBody GastoGuardarDTO gastoGuardar) {

        GastoSalidaDTO gasto =
                gastoService.crear(gastoGuardar);

        return ResponseEntity.ok(gasto);
    }


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<GastoSalidaDTO> editar(
            @PathVariable Long id,
            @Valid
            @RequestBody GastoModificarDTO gastoModificar) {

        gastoModificar.setId(id);

        GastoSalidaDTO gasto =
                gastoService.editar(gastoModificar);

        return ResponseEntity.ok(gasto);
    }


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable Long id) {

        gastoService.eliminarPorId(id);

        return ResponseEntity.ok(
                "Gasto desactivado correctamente"
        );
    }
}