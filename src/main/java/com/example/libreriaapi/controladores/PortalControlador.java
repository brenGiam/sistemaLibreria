package com.example.libreriaapi.controladores;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.libreriaapi.entidades.Usuario;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.servicios.UsuarioServicio;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class PortalControlador {

    private final UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "registro_usuario.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam MultipartFile archivo, @RequestParam String email, @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String password,
            String password2, ModelMap modelo) {

        try {
            usuarioServicio.registrarUsuario(archivo, email, nombre, apellido, password, password2);
            modelo.put("exito",
                    "Usuario registrado correctamente! Ya podés iniciar sesión! Redireccionando en 3 segundos");
            return "registro_usuario.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            return "registro_usuario.html";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña inválidos!");
        }
        return "login.html";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().toString().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        return "inicio.html";
    }

    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("usuario", usuario);
        return "modificar_usuario.html";
    }

    @PutMapping("/perfil/{id}")
    public String modificar(MultipartFile archivo, @PathVariable UUID id,
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String password,
            @RequestParam String password2, ModelMap modelo, HttpSession session) {
        try {
            usuarioServicio.modificarUsuario(archivo, id, email, nombre, apellido, password,
                    password2);
            Usuario usuarioActualizado = usuarioServicio.obtenerUsuario(id);
            session.setAttribute("usuariosession", usuarioActualizado);
            modelo.put("exito", "Usuario actualizado correctamente!");
            return "inicio.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            return "modificar_usuario.html";
        }
    }
}