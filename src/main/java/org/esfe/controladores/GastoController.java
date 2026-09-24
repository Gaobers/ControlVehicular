package org.esfe.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.dtos.GastoRegistroRequest;
import org.esfe.dtos.GastoResponse;
import org.esfe.servicio.interfaces.IGastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final IGastoService gastoService;

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gastoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody GastoRegistroRequest request) {
        return ResponseEntity.ok(gastoService.actualizarGasto(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GastoResponse>> obtenerTodos() {
        return ResponseEntity.ok(gastoService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<GastoResponse> registrar(@Valid @RequestBody GastoRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gastoService.registrarGasto(request));
    }

    @GetMapping("/auxiliar")
    public ResponseEntity<List<GastoResponse>> obtenerAuxiliar() {
        return ResponseEntity.ok(gastoService.obtenerAuxiliar());
    }
}