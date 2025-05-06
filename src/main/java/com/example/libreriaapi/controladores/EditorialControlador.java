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

import com.example.libreriaapi.entidades.Editorial;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.EditorialCreateDTO;
import com.example.libreriaapi.modelos.EditorialListarDTO;
import com.example.libreriaapi.modelos.EditorialModificarDTO;
import com.example.libreriaapi.servicios.EditorialServicio;

import lombok.Data;

@Data
@Controller
@RequestMapping("/editorial")
public class EditorialControlador {
    private final EditorialServicio editorialServicio;

    @GetMapping("/crear")
    public String mostrarFormularioCrearEditorial(ModelMap modelo) {
        modelo.addAttribute("editorial", new EditorialCreateDTO());
        modelo.addAttribute("esEditorial", true);
        modelo.addAttribute("subtitulo", "Crear Editorial");
        return "formulario_crear.html";
    }

    @PostMapping("/crear")
    public String crearEditorial(@ModelAttribute EditorialCreateDTO editorialDTO,
            RedirectAttributes redirectAttributes,
            ModelMap modelo) {
        try {
            editorialServicio.crearEditorial(editorialDTO);
            redirectAttributes.addFlashAttribute("exito", "Editorial creada exitosamente");
            return "redirect:/editorial/listarTodas";
        } catch (MiException ex) {
            modelo.addAttribute("editorial", editorialDTO);
            modelo.addAttribute("esEditorial", true);
            modelo.addAttribute("subtitulo", "Crear Editorial");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_crear.html";
        }
    }

    @GetMapping("/listarTodas")
    public String listarEditoriales(
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

        Page<EditorialListarDTO> paginaEditoriales = editorialServicio.listarEditoriales(pageable);

        modelo.addAttribute("editoriales", paginaEditoriales.getContent());
        modelo.addAttribute("pagina", paginaEditoriales);
        modelo.addAttribute("tipoEntidad", "editorial");

        return "lista_vista.html";
    }

    @GetMapping("/buscar")
    public String buscarEditoriales(@RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            ModelMap model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<EditorialListarDTO> paginasEditoriales = editorialServicio.buscarPorNombre(query, pageable);

        model.addAttribute("editoriales", paginasEditoriales.getContent());
        model.addAttribute("pagina", paginasEditoriales);
        model.addAttribute("query", query);
        model.addAttribute("tipoEntidad", "editorial");

        return "lista_vista.html";
    }

    @GetMapping("/{id}")
    public String verEditorial(@PathVariable UUID id, ModelMap modelo) {
        Editorial editorial = editorialServicio.obtenerEditorial(id);
        modelo.addAttribute("editorial", editorial);
        modelo.addAttribute("tipoEntidad", "editorial");
        return "detalle_vista.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable UUID id, ModelMap modelo) {
        modelo.put("editorial", editorialServicio.obtenerEditorial(id));
        modelo.addAttribute("esEditorial", true);
        modelo.addAttribute("subtitulo", "Modificar Editorial");
        return "formulario_modificar.html";
    }

    @PutMapping("/modificar/{id}")
    public String modificar(@PathVariable UUID id, EditorialModificarDTO editorialModificarDTO,
            RedirectAttributes redirectAttributes, ModelMap modelo) {
        try {
            editorialModificarDTO.setId(id);
            editorialServicio.modificarEditorial(editorialModificarDTO);
            redirectAttributes.addFlashAttribute("exito", "Editorial modificada exitosamente");
            return "redirect:/editorial/listarTodas";
        } catch (MiException ex) {
            modelo.put("editorial", editorialServicio.obtenerEditorial(id));
            modelo.addAttribute("esEditorial", true);
            modelo.addAttribute("subtitulo", "Modificar Editorial");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_modificar.html";
        }
    }

    @DeleteMapping("eliminar/{id}") // elimina definitivamente
    public String eliminar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            editorialServicio.eliminarEditorial(id);
            redirectAttributes.addFlashAttribute("exito", "Editorial eliminada exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/editorial/listarTodas";
    }

    @PatchMapping("baja/{id}") // elimina dando de baja el atributo EditorialActivo
    public String darDeBaja(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            editorialServicio.darDeBajaEditorial(id);
            redirectAttributes.addFlashAttribute("exito", "Editorial dada de baja exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/editorial/listarTodas";
    }

    @PatchMapping("alta/{id}")
    public String darDeAlta(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            editorialServicio.darDeAltaEditorial(id);
            redirectAttributes.addFlashAttribute("exito", "Editorial dada de alta exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/editorial/listarTodas";
    }
}
