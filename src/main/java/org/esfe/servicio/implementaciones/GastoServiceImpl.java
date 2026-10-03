package org.esfe.servicio.implementaciones;

import jakarta.persistence.EntityNotFoundException;

import org.esfe.dtos.gasto.GastoGuardarDTO;
import org.esfe.dtos.gasto.GastoModificarDTO;
import org.esfe.dtos.gasto.GastoSalidaDTO;

import org.esfe.modelos.Gasto;

import org.esfe.repositorios.GastoRepository;

import org.esfe.servicio.interfaces.IGastoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GastoServiceImpl
        implements IGastoService {

    private final GastoRepository gastoRepository;


    public GastoServiceImpl(
            GastoRepository gastoRepository
    ) {

        this.gastoRepository =
                gastoRepository;
    }


    @Override
    public Page<GastoSalidaDTO>
    obtenerTodosPaginados(
            Pageable pageable
    ) {

        return gastoRepository
                .findAll(pageable)
                .map(this::convertirASalida);
    }


    @Override
    public List<GastoSalidaDTO>
    obtenerTodos() {

        return gastoRepository
                .findAll()
                .stream()
                .map(this::convertirASalida)
                .toList();
    }


    @Override
    public GastoSalidaDTO obtenerPorId(
            Long id
    ) {

        Gasto gasto =
                buscarEntidadPorId(id);

        return convertirASalida(
                gasto
        );
    }


    @Override
    public GastoSalidaDTO crear(
            GastoGuardarDTO gastoGuardar
    ) {

        Gasto gasto =
                new Gasto();


        gasto.setVehiculoId(
                gastoGuardar.getVehiculoId()
        );


        gasto.setCategoriaId(
                gastoGuardar.getCategoriaId()
        );


        gasto.setMonto(
                gastoGuardar.getMonto()
        );


        gasto.setMoneda(
                normalizarMoneda(
                        gastoGuardar.getMoneda()
                )
        );


        gasto.setFecha(
                gastoGuardar.getFecha()
        );


        gasto.setDescripcion(
                normalizarTextoOpcional(
                        gastoGuardar.getDescripcion()
                )
        );


        gasto.setNumeroComprobante(
                normalizarTextoOpcional(
                        gastoGuardar
                                .getNumeroComprobante()
                )
        );


        gasto.setProveedor(
                normalizarTextoOpcional(
                        gastoGuardar.getProveedor()
                )
        );


        /*
         * El usuario que registró el gasto
         * se asociará posteriormente desde
         * la autenticación/JWT.
         *
         * La BD permite NULL.
         */
        gasto.setRegistradoPor(null);


        /*
         * Todo gasto nuevo inicia activo.
         */
        gasto.setActivo(true);


        Gasto guardado =
                gastoRepository.save(gasto);


        return convertirASalida(
                guardado
        );
    }


    @Override
    public GastoSalidaDTO editar(
            GastoModificarDTO gastoModificar
    ) {

        Gasto gasto =
                buscarEntidadPorId(
                        gastoModificar.getId()
                );


        gasto.setVehiculoId(
                gastoModificar.getVehiculoId()
        );


        gasto.setCategoriaId(
                gastoModificar.getCategoriaId()
        );


        gasto.setMonto(
                gastoModificar.getMonto()
        );


        gasto.setMoneda(
                normalizarMoneda(
                        gastoModificar.getMoneda()
                )
        );


        gasto.setFecha(
                gastoModificar.getFecha()
        );


        gasto.setDescripcion(
                normalizarTextoOpcional(
                        gastoModificar.getDescripcion()
                )
        );


        gasto.setNumeroComprobante(
                normalizarTextoOpcional(
                        gastoModificar
                                .getNumeroComprobante()
                )
        );


        gasto.setProveedor(
                normalizarTextoOpcional(
                        gastoModificar.getProveedor()
                )
        );


        gasto.setActivo(
                gastoModificar.getActivo()
        );


        Gasto actualizado =
                gastoRepository.save(gasto);


        return convertirASalida(
                actualizado
        );
    }


    @Override
    public void eliminarPorId(
            Long id
    ) {

        Gasto gasto =
                buscarEntidadPorId(id);


        /*
         * BORRADO LÓGICO.
         *
         * Nunca hacemos deleteById()
         * porque necesitamos conservar
         * el historial financiero.
         */
        gasto.setActivo(false);


        gastoRepository.save(
                gasto
        );
    }


    private Gasto buscarEntidadPorId(
            Long id
    ) {

        return gastoRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Gasto no encontrado con id: "
                                                + id
                                )
                );
    }


    private String normalizarMoneda(
            String moneda
    ) {

        if (
                moneda == null ||
                        moneda.isBlank()
        ) {

            return "USD";
        }


        return moneda
                .trim()
                .toUpperCase();
    }


    private String normalizarTextoOpcional(
            String texto
    ) {

        if (
                texto == null ||
                        texto.isBlank()
        ) {

            return null;
        }


        return texto.trim();
    }


    private GastoSalidaDTO convertirASalida(
            Gasto gasto
    ) {

        GastoSalidaDTO salida =
                new GastoSalidaDTO();


        salida.setId(
                gasto.getId()
        );


        salida.setVehiculoId(
                gasto.getVehiculoId()
        );


        salida.setCategoriaId(
                gasto.getCategoriaId()
        );


        salida.setRegistradoPor(
                gasto.getRegistradoPor()
        );


        salida.setMonto(
                gasto.getMonto()
        );


        salida.setMoneda(
                gasto.getMoneda()
        );


        salida.setFecha(
                gasto.getFecha()
        );


        salida.setDescripcion(
                gasto.getDescripcion()
        );


        salida.setNumeroComprobante(
                gasto.getNumeroComprobante()
        );


        salida.setProveedor(
                gasto.getProveedor()
        );


        salida.setActivo(
                gasto.getActivo()
        );


        salida.setFechaRegistro(
                gasto.getFechaRegistro()
        );


        salida.setFechaActualizacion(
                gasto.getFechaActualizacion()
        );


        return salida;
    }
}