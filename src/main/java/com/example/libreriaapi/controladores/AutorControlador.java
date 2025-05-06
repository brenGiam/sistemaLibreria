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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.libreriaapi.entidades.Autor;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.AutorCreateDTO;
import com.example.libreriaapi.modelos.AutorListarDTO;
import com.example.libreriaapi.modelos.AutorModificarDTO;
import com.example.libreriaapi.servicios.AutorServicio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/autor")
public class AutorControlador {

    private final AutorServicio autorServicio;

    @GetMapping("/crear")
    public String mostrarFormularioCrearAutor(ModelMap modelo) {
        modelo.addAttribute("autor", new AutorCreateDTO());
        modelo.addAttribute("esAutor", true);
        modelo.addAttribute("subtitulo", "Crear Autor");
        return "formulario_crear.html";
    }

    @PostMapping("/crear")
    public String crearAutor(@ModelAttribute AutorCreateDTO autorDTO,
            RedirectAttributes redirectAttributes,
            ModelMap modelo) {
        try {
            autorServicio.crearAutor(autorDTO);
            redirectAttributes.addFlashAttribute("exito", "Autor creado exitosamente");
            return "redirect:/autor/listarTodos";
        } catch (MiException ex) {
            modelo.addAttribute("autor", autorDTO);
            modelo.addAttribute("esAutor", true);
            modelo.addAttribute("subtitulo", "Crear Autor");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_crear.html";
        }
    }

    @GetMapping("/listarTodos")
    public String listarAutores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String ordenar,
            ModelMap modelo) {

        int pageSize = 5; // Cantidad de autores por página
        Pageable pageable;

        // Si se solicita ordenar por nombre
        if ("nombre".equals(ordenar)) {
            pageable = PageRequest.of(page, pageSize, Sort.by("nombre").ascending());
            modelo.addAttribute("ordenar", "nombre"); // Para mantener el parámetro en la vista
        } else {
            pageable = PageRequest.of(page, pageSize);
        }

        Page<AutorListarDTO> paginaAutores = autorServicio.listarAutores(pageable);

        modelo.addAttribute("autores", paginaAutores.getContent());
        modelo.addAttribute("pagina", paginaAutores);
        modelo.addAttribute("tipoEntidad", "autor");

        return "lista_vista.html";
    }

    @GetMapping("/buscar")
    public String buscarAutores(@RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            ModelMap model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<AutorListarDTO> paginasAutores = autorServicio.buscarPorNombre(query, pageable);

        model.addAttribute("autores", paginasAutores.getContent());
        model.addAttribute("pagina", paginasAutores);
        model.addAttribute("query", query);
        model.addAttribute("tipoEntidad", "autor");

        return "lista_vista.html";
    }

    @GetMapping("/{id}")
    public String verAutor(@PathVariable UUID id, ModelMap modelo) {
        Autor autor = autorServicio.obtenerAutor(id);
        modelo.addAttribute("autor", autor);
        modelo.addAttribute("tipoEntidad", "autor");
        return "detalle_vista.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable UUID id, ModelMap modelo) {
        modelo.put("autor", autorServicio.obtenerAutor(id));
        modelo.addAttribute("esAutor", true);
        modelo.addAttribute("subtitulo", "Modificar Autor");
        return "formulario_modificar.html";
    }

    @PutMapping("/modificar/{id}")
    public String modificar(@PathVariable UUID id, AutorModificarDTO autorModificarDTO,
            RedirectAttributes redirectAttributes, ModelMap modelo) {
        try {
            autorModificarDTO.setId(id);
            autorServicio.modificarAutor(autorModificarDTO);
            redirectAttributes.addFlashAttribute("exito", "Autor modificado exitosamente");
            return "redirect:/autor/listarTodos";
        } catch (MiException ex) {
            modelo.put("autor", autorServicio.obtenerAutor(id));
            modelo.addAttribute("esAutor", true);
            modelo.addAttribute("subtitulo", "Modificar Autor");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_modificar.html";
        }
    }

    @DeleteMapping("eliminar/{id}") // elimina definitivamente
    public String eliminar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            autorServicio.eliminaAutor(id);
            redirectAttributes.addFlashAttribute("exito", "Autor eliminado exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/autor/listarTodos";
    }

    @PatchMapping("baja/{id}") // elimina dando de baja el atributo AutorActivo
    public String darDeBaja(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            autorServicio.darDeBajaAutor(id);
            redirectAttributes.addFlashAttribute("exito", "Autor dado de baja exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/autor/listarTodos";
    }

    @PatchMapping("alta/{id}")
    public String darDeAlta(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            autorServicio.darDeAltaAutor(id);
            redirectAttributes.addFlashAttribute("exito", "Autor dado de alta exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/autor/listarTodos";
    }

}
