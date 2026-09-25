package org.esfe.controladores;

import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoGuardar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoModificar;
import org.esfe.dtos.RecordatorioMantenimiento.RecordatorioMantenimientoSalida;
import org.esfe.servicio.interfaces.IRecordatorioMantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recordatorios")
public class RecordatorioMantenimientoController {

    @Autowired
    private IRecordatorioMantenimientoService recordatorioService;

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<RecordatorioMantenimientoSalida>> mostrarTodosPaginados(
            Pageable pageable) {

        Page<RecordatorioMantenimientoSalida> recordatorios =
                recordatorioService.obtenerTodosPaginados(pageable);

        if (recordatorios.hasContent()) {
            return ResponseEntity.ok(recordatorios);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping("/lista")
    public ResponseEntity<List<RecordatorioMantenimientoSalida>> mostrarTodos() {

        List<RecordatorioMantenimientoSalida> recordatorios =
                recordatorioService.obtenerTodos();

        if (!recordatorios.isEmpty()) {
            return ResponseEntity.ok(recordatorios);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<RecordatorioMantenimientoSalida> buscarPorId(
            @PathVariable Long id) {

        RecordatorioMantenimientoSalida recordatorio =
                recordatorioService.obtenerPorId(id);

        if (recordatorio != null) {
            return ResponseEntity.ok(recordatorio);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<RecordatorioMantenimientoSalida> crear(
            @RequestBody RecordatorioMantenimientoGuardar recordatorioGuardar) {

        RecordatorioMantenimientoSalida recordatorio =
                recordatorioService.crear(recordatorioGuardar);

        return ResponseEntity.ok(recordatorio);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<RecordatorioMantenimientoSalida> editar(
            @PathVariable Long id,
            @RequestBody RecordatorioMantenimientoModificar recordatorioModificar) {

        recordatorioModificar.setId(id);

        RecordatorioMantenimientoSalida recordatorio =
                recordatorioService.editar(recordatorioModificar);

        return ResponseEntity.ok(recordatorio);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        recordatorioService.eliminarPorId(id);

        return ResponseEntity.ok(
                "Recordatorio de mantenimiento desactivado correctamente"
        );
    }
}