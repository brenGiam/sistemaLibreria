package com.example.libreriaapi.controladores;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.libreriaapi.entidades.Usuario;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.servicios.UsuarioServicio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private final UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard")
    public String panelAdministrativo() {
        return "panel_admin.html";
    }

    @GetMapping("/listarUsuarios")
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String ordenar,
            ModelMap modelo) {

        int pageSize = 5; // Cantidad de autores por página
        Pageable pageable;

        // Si se solicita ordenar por nombre
        if ("apellido".equals(ordenar)) {
            pageable = PageRequest.of(page, pageSize, Sort.by("apellido").ascending());
            modelo.addAttribute("ordenar", "apellido"); // Para mantener el parámetro en la vista
        } else {
            pageable = PageRequest.of(page, pageSize);
        }

        Page<Usuario> paginaUsuarios = usuarioServicio.listarUsuarios(pageable);

        modelo.addAttribute("usuarios", paginaUsuarios.getContent());
        modelo.addAttribute("pagina", paginaUsuarios);
        modelo.addAttribute("tipoEntidad", "usuario");

        return "lista_vista.html";
    }

    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            ModelMap model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<Usuario> paginasUsuarios = usuarioServicio.buscarPorNombreMail(query, pageable);

        model.addAttribute("usuarios", paginasUsuarios.getContent());
        model.addAttribute("pagina", paginasUsuarios);
        model.addAttribute("query", query);
        model.addAttribute("tipoEntidad", "usuario");

        return "lista_vista.html";
    }

    @GetMapping("/modificarRol/{id}")
    public String cambiarRol(@PathVariable UUID id) {
        usuarioServicio.cambiarRol(id);
        return "redirect:/admin/listarUsuarios";
    }

    @GetMapping("/modificar/{id}")
    public String modificarUsuario(@PathVariable UUID id, ModelMap modelo) {
        Usuario usuario = usuarioServicio.obtenerUsuario(id);
        modelo.addAttribute("usuario", usuario);
        return "modificar_usuario.html";
    }

    @PutMapping("/modificar/{id}")
    public String modificar(MultipartFile archivo, @PathVariable UUID id,
            @RequestParam String email, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioServicio.modificarUsuario(archivo, id, email, nombre, apellido, password, password2);
            redirectAttributes.addFlashAttribute("exito", "Usuario modificado exitosamente");
            return "redirect:/admin/listarUsuarios";
        } catch (MiException ex) {
            System.out.println("Se capturó una excepción: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("nombre", nombre);
            redirectAttributes.addFlashAttribute("apellido", apellido);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/admin/listarUusarios/" + id;
        }
    }

    @DeleteMapping("/eliminarUsuario/{id}")
    public String eliminar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            usuarioServicio.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("exito", "Usuario eliminado exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/listarUsuarios";
    }

}
