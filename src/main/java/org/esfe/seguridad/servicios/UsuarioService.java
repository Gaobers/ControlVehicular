package org.esfe.seguridad.servicios;

import org.esfe.enums.EstadoUsuario;
import org.esfe.enums.RolUsuario;
import org.esfe.seguridad.dtos.UsuarioLogin;
import org.esfe.seguridad.dtos.UsuarioRegistrar;
import org.esfe.seguridad.dtos.UsuarioToken;
import org.esfe.seguridad.modelos.Usuario;
import org.esfe.seguridad.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UsuarioToken login(UsuarioLogin loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getClave()
                )
        );

        Usuario usuario = usuarioRepository
                .findByCorreo(loginRequest.getCorreo())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "El usuario no fue encontrado"
                        )
                );

        String token = jwtService.getToken(usuario);

        return UsuarioToken.builder()
                .token(token)
                .build();
    }

    public UsuarioToken registro(UsuarioRegistrar registroRequest) {

        if (usuarioRepository
                .findByCorreo(registroRequest.getCorreo())
                .isPresent()) {

            throw new RuntimeException(
                    "Ya existe un usuario registrado con ese correo"
            );
        }

        Usuario usuario = Usuario.builder()
                .nombreCompleto(registroRequest.getNombreCompleto())
                .telefono(registroRequest.getTelefono())
                .correo(registroRequest.getCorreo())
                .passwordHash(
                        passwordEncoder.encode(
                                registroRequest.getClave()
                        )
                )
                .rol(RolUsuario.CLIENTE)
                .estado(EstadoUsuario.ACTIVO)
                .build();

        usuarioRepository.save(usuario);

        return UsuarioToken.builder()
                .token(jwtService.getToken(usuario))
                .build();
    }
}