package org.esfe.seguridad.servicios;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.esfe.seguridad.modelos.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public String getToken(Usuario usuario) {

        List<String> roles = List.of(
                usuario.getRol().name()
        );

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", roles);

        return generarToken(extraClaims, usuario);
    }

    private String generarToken(
            Map<String, Object> extraClaims,
            UserDetails usuario
    ) {

        return Jwts.builder()
                .claims(extraClaims)
                .subject(usuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60
                        )
                )
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(
                token,
                Claims::getSubject
        );
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        final String correo =
                getUsernameFromToken(token);

        return correo.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private Claims getAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        final Claims claims =
                getAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(
                token,
                Claims::getExpiration
        );
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token)
                .before(new Date());
    }
}