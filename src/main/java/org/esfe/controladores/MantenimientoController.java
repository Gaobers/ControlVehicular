package org.esfe.controladores;

import org.esfe.dtos.mantenimiento.MantenimientoGuardarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoModificarDTO;
import org.esfe.dtos.mantenimiento.MantenimientoSalidaDTO;
import org.esfe.enums.EstadoMantenimiento;
import org.esfe.servicio.interfaces.IMantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <- Importante agregarlo
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mantenimientos")
@CrossOrigin(origins = "*") // Permite peticiones sin bloqueo CORS/Security básico
public class MantenimientoController {

    @Autowired
    private IMantenimientoService mantenimientoService;

    @GetMapping("/lista")
    public ResponseEntity<List<MantenimientoSalidaDTO>> mostrarTodos(
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) EstadoMantenimiento estado) {

        List<MantenimientoSalidaDTO> lista =
                mantenimientoService.obtenerTodos(vehiculoId, estado);

        if (!lista.isEmpty()) {
            return ResponseEntity.ok(lista);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoSalidaDTO> buscarPorId(
            @PathVariable Long id) {

        Optional<MantenimientoSalidaDTO> mantenimiento =
                mantenimientoService.obtenerPorId(id);

        if (mantenimiento.isPresent()) {
            return ResponseEntity.ok(mantenimiento.get());
        }

        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<MantenimientoSalidaDTO> crear(
            @RequestBody MantenimientoGuardarDTO mantenimientoGuardar) {

        MantenimientoSalidaDTO creado =
                mantenimientoService.crear(mantenimientoGuardar);

        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping
    public ResponseEntity<MantenimientoSalidaDTO> modificar(
            @RequestBody MantenimientoModificarDTO mantenimientoModificar) {

        MantenimientoSalidaDTO modificado =
                mantenimientoService.modificar(mantenimientoModificar);

        return ResponseEntity.ok(modificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        boolean eliminado = mantenimientoService.eliminarPorId(id);

        if (eliminado) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}