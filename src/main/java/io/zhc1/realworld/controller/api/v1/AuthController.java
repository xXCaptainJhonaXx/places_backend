package io.zhc1.realworld.controller.api.v1;

import io.zhc1.realworld.model.Usuario;
import io.zhc1.realworld.repository.UsuarioRepository;
import io.zhc1.realworld.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager; // Para validar login

    @Autowired
    private PasswordEncoder passwordEncoder; // Para cifrar al registrar

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            // Spring Security valida automáticamente el usuario y la contraseña cifrada
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credenciales.get("usuario"),
                            credenciales.get("contrasena")
                    )
            );

            // Si llegamos aquí, el usuario es válido
            String token = JwtUtil.generarToken(auth.getName());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Login exitoso",
                    "token", token
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario o contraseña incorrectos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuarioConPersona) {
        if (usuarioConPersona == null || usuarioConPersona.getPersona() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos incompletos"));
        }

        // Cifrar la contraseña ANTES de guardar
        String passwordCifrada = passwordEncoder.encode(usuarioConPersona.getContrasena());
        usuarioConPersona.setContrasena(passwordCifrada);
        usuarioConPersona.setRol("USER");

        try {
            usuarioRepository.save(usuarioConPersona);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al registrar: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Registrado exitosamente"));
    }
    @PatchMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> credenciales, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Validar la contraseña actual
        if (!passwordEncoder.matches(credenciales.get("passwordActual"), usuario.getContrasena())) {
            return ResponseEntity.status(401).body(Map.of("error", "Contraseña actual incorrecta"));
        }

        // 2. Cifrar y guardar la nueva
        usuario.setContrasena(passwordEncoder.encode(credenciales.get("nuevaPassword")));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
    }
}