package com.example.libreriaapi.controladores;

import java.util.List;

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
import com.example.libreriaapi.entidades.Editorial;
import com.example.libreriaapi.entidades.Libro;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.LibroCreateDTO;
import com.example.libreriaapi.modelos.LibroListarDTO;
import com.example.libreriaapi.modelos.LibroModificarDTO;
import com.example.libreriaapi.servicios.AutorServicio;
import com.example.libreriaapi.servicios.EditorialServicio;
import com.example.libreriaapi.servicios.LibroServicio;

import lombok.Data;

@Data
@Controller
@RequestMapping("/libro")
public class LibroControlador {

    private final LibroServicio libroServicio;
    private final AutorServicio autorServicio;
    private final EditorialServicio editorialServicio;

    @GetMapping("/crear")
    public String mostrarFormularioCrearLibro(ModelMap modelo) {
        modelo.addAttribute("libro", new LibroCreateDTO());
        modelo.addAttribute("autores", autorServicio.listarTodosLosAutores());
        modelo.addAttribute("editoriales", editorialServicio.listarTodasLasEditoriales());
        modelo.addAttribute("esLibro", true);
        modelo.addAttribute("subtitulo", "Crear Libro");
        return "formulario_crear.html";
    }

    @PostMapping("/crear")
    public String crearLibro(@ModelAttribute LibroCreateDTO libroDTO,
            RedirectAttributes redirectAttributes,
            ModelMap modelo) {
        try {
            libroServicio.crearLibro(libroDTO);
            redirectAttributes.addFlashAttribute("exito", "Libro creado exitosamente");
            return "redirect:/libro/listarTodos";
        } catch (MiException ex) {
            modelo.addAttribute("libro", libroDTO);
            modelo.addAttribute("autores", autorServicio.listarTodosLosAutores());
            modelo.addAttribute("editoriales", editorialServicio.listarTodasLasEditoriales());
            modelo.addAttribute("esLibro", true);
            modelo.addAttribute("subtitulo", "Crear Libro");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_crear.html";
        }
    }

    @GetMapping("/listarTodos")
    public String listarLibros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String ordenar,
            ModelMap modelo) {

        int pageSize = 5; // Cantidad de libros por página
        Pageable pageable;

        // Si se solicita ordenar por título
        if ("titulo".equals(ordenar)) {
            pageable = PageRequest.of(page, pageSize, Sort.by("titulo").ascending());
            modelo.addAttribute("ordenar", "titulo"); // Para mantener el parámetro en la vista
        } else {
            pageable = PageRequest.of(page, pageSize);
        }

        Page<LibroListarDTO> paginaLibros = libroServicio.listarLibros(pageable);

        modelo.addAttribute("libros", paginaLibros.getContent());
        modelo.addAttribute("pagina", paginaLibros);
        modelo.addAttribute("tipoEntidad", "libro");

        return "lista_vista.html";
    }

    @GetMapping("/buscar")
    public String buscarLibros(@RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            ModelMap model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<LibroListarDTO> paginasLibros = libroServicio.buscarPorTituloAutorEditorial(query, pageable);

        model.addAttribute("libros", paginasLibros.getContent());
        model.addAttribute("pagina", paginasLibros);
        model.addAttribute("query", query);
        model.addAttribute("tipoEntidad", "libro");

        return "lista_vista.html";
    }

    @GetMapping("/{isbn}")
    public String verLibro(@PathVariable Long isbn, ModelMap modelo) {
        Libro libro = libroServicio.obtenerLibro(isbn);
        modelo.addAttribute("libro", libro);
        modelo.addAttribute("tipoEntidad", "libro");
        return "detalle_vista.html";
    }

    @GetMapping("/modificar/{isbn}")
    public String modificar(@PathVariable Long isbn, ModelMap modelo) {
        modelo.put("libro", libroServicio.obtenerLibro(isbn));
        List<Autor> autores = autorServicio.listarTodosLosAutores();
        List<Editorial> editoriales = editorialServicio.listarTodasLasEditoriales();
        modelo.addAttribute("autores", autores);
        modelo.addAttribute("editoriales", editoriales);
        modelo.addAttribute("esLibro", true);
        modelo.addAttribute("subtitulo", "Modificar Libro");
        return "formulario_modificar.html";
    }

    @PutMapping("/modificar/{isbn}")
    public String modificar(@PathVariable Long isbn, @ModelAttribute LibroModificarDTO libroModificarDTO,
            RedirectAttributes redirectAttributes, ModelMap modelo) {
        try {
            libroModificarDTO.setIsbnOriginal(isbn);
            libroServicio.modificarLibro(libroModificarDTO);
            redirectAttributes.addFlashAttribute("exito", "Libro modificado exitosamente");
            return "redirect:/libro/listarTodos";
        } catch (MiException ex) {
            modelo.put("libro", libroServicio.obtenerLibro(isbn));
            List<Autor> autores = autorServicio.listarTodosLosAutores();
            List<Editorial> editoriales = editorialServicio.listarTodasLasEditoriales();
            modelo.addAttribute("autores", autores);
            modelo.addAttribute("editoriales", editoriales);
            modelo.addAttribute("esLibro", true);
            modelo.addAttribute("subtitulo", "Modificar Libro");
            modelo.addAttribute("error", ex.getMessage());
            return "formulario_modificar.html";
        }
    }

    @DeleteMapping("eliminar/{isbn}") // elimina definitivamente
    public String eliminar(@PathVariable Long isbn, RedirectAttributes redirectAttributes) {
        try {
            libroServicio.eliminarLibro(isbn);
            redirectAttributes.addFlashAttribute("exito", "Libro eliminado exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/libro/listarTodos";
    }

    @PatchMapping("baja/{isbn}") // elimina dando de baja el atributo LibroActivo
    public String darDeBaja(@PathVariable Long isbn, RedirectAttributes redirectAttributes) {
        try {
            libroServicio.darDeBajaLibro(isbn);
            redirectAttributes.addFlashAttribute("exito", "Libro dado de baja exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/libro/listarTodos";
    }

    @PatchMapping("alta/{isbn}")
    public String darDeAlta(@PathVariable Long isbn, RedirectAttributes redirectAttributes) {
        try {
            libroServicio.darDeAltaLibro(isbn);
            redirectAttributes.addFlashAttribute("exito", "Libro dado de alta exitosamente");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/libro/listarTodos";
    }

}
