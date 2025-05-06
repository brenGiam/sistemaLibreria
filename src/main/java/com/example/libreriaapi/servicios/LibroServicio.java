package com.example.libreriaapi.servicios;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.libreriaapi.entidades.Autor;
import com.example.libreriaapi.entidades.Editorial;
import com.example.libreriaapi.entidades.Imagen;
import com.example.libreriaapi.entidades.Libro;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.LibroCreateDTO;
import com.example.libreriaapi.modelos.LibroListarDTO;
import com.example.libreriaapi.modelos.LibroModificarDTO;
import com.example.libreriaapi.repositorios.AutorRepositorio;
import com.example.libreriaapi.repositorios.EditorialRepositorio;
import com.example.libreriaapi.repositorios.LibroRepositorio;

import lombok.Data;

@Data
@Service
public class LibroServicio {
    private final LibroRepositorio libroRepositorio;
    private final EditorialRepositorio editorialRepositorio;
    private final AutorRepositorio autorRepositorio;
    private final ImagenServicio imagenServicio;

    @Transactional
    public void crearLibro(LibroCreateDTO libroCreateDTO) {
        Autor autor = autorRepositorio.findById(libroCreateDTO.getIdAutor())
                .orElseThrow(() -> new MiException("No se encontró el autor"));
        Editorial editorial = editorialRepositorio.findById(libroCreateDTO.getIdEditorial())
                .orElseThrow(() -> new MiException("No se encontró la editorial"));

        Libro libroNvo = new Libro();

        libroNvo.setIsbn(libroCreateDTO.getIsbn());
        libroNvo.setTitulo(libroCreateDTO.getTitulo());
        libroNvo.setEjemplares(libroCreateDTO.getEjemplares());
        libroNvo.setLibroActivo(true);
        libroNvo.setAutor(autor);
        libroNvo.setEditorial(editorial);

        // Procesar la imagen si se proporciona
        if (libroCreateDTO.getImagen() != null && !libroCreateDTO.getImagen().isEmpty()) {
            try {
                Imagen imagen = imagenServicio.guardar(libroCreateDTO.getImagen());
                libroNvo.setImagen(imagen);
            } catch (Exception e) {
                throw new MiException("Error al guardar la imagen: " + e.getMessage());
            }
        }

        libroRepositorio.save(libroNvo);
    }

    @Transactional(readOnly = true)
    public Page<LibroListarDTO> listarLibros(Pageable pageable) {
        return libroRepositorio.encontrarTodos(pageable);
    }

    public Page<LibroListarDTO> buscarPorTituloAutorEditorial(String query, Pageable pageable) {
        return libroRepositorio.buscarPorTituloAutorEditorial(query, pageable);
    }

    @Transactional(readOnly = true)
    public Libro obtenerLibro(Long isbn) {
        Optional<Libro> respuesta = libroRepositorio.findById(isbn);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new MiException("No se encontró el libro con el ISBN: " + isbn);
        }
    }

    @Transactional
    public void modificarLibro(LibroModificarDTO libroModificarDTO) {
        Optional<Libro> respuesta = libroRepositorio.findById(libroModificarDTO.getIsbnOriginal());

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            boolean isbnCambiado = !libroModificarDTO.getIsbnOriginal().equals(libroModificarDTO.getIsbn());

            if (isbnCambiado) {
                // Verificar que el nuevo ISBN no esté ya en uso
                if (libroRepositorio.existsById(libroModificarDTO.getIsbn())) {
                    throw new MiException("Ya existe un libro con el ISBN: " + libroModificarDTO.getIsbn());
                }

                Libro nuevoLibro = new Libro();
                nuevoLibro.setIsbn(libroModificarDTO.getIsbn());
                nuevoLibro.setTitulo(libroModificarDTO.getTitulo());
                nuevoLibro.setEjemplares(libroModificarDTO.getEjemplares());
                nuevoLibro.setLibroActivo(libro.isLibroActivo()); // Mantener el estado activo

                Autor autor = autorRepositorio.findById(libroModificarDTO.getAutorId())
                        .orElseThrow(() -> new MiException("No se encontró el autor"));
                nuevoLibro.setAutor(autor);

                Editorial editorial = editorialRepositorio.findById(libroModificarDTO.getEditorialId())
                        .orElseThrow(() -> new MiException("No se encontró la editorial"));
                nuevoLibro.setEditorial(editorial);

                if (libroModificarDTO.getImagen() != null && !libroModificarDTO.getImagen().isEmpty()) {
                    try {
                        Imagen imagen = imagenServicio.guardar(libroModificarDTO.getImagen());
                        nuevoLibro.setImagen(imagen);
                    } catch (Exception e) {
                        throw new MiException("Error al guardar la imagen: " + e.getMessage());
                    }
                } else if (libro.getImagen() != null) {
                    // Disociar la imagen del libro antiguo antes de eliminar
                    // para evitar restricciones de clave única
                    Imagen imagenAntigua = libro.getImagen();
                    libro.setImagen(null);
                    libroRepositorio.save(libro); // Guardar el libro sin la imagen

                    try {
                        Imagen nuevaImagen = imagenServicio.duplicar(imagenAntigua);
                        nuevoLibro.setImagen(nuevaImagen);

                    } catch (Exception e) {
                        throw new MiException("Error al transferir la imagen: " + e.getMessage());
                    }
                }

                libroRepositorio.save(nuevoLibro);

                // Eliminar el libro con el ISBN antiguo
                libroRepositorio.deleteById(libroModificarDTO.getIsbnOriginal());

            } else {
                // Para cuando no cambia el ISBN
                libro.setTitulo(libroModificarDTO.getTitulo());
                libro.setEjemplares(libroModificarDTO.getEjemplares());
                libro.setLibroActivo(true);

                Autor autor = autorRepositorio.findById(libroModificarDTO.getAutorId())
                        .orElseThrow(() -> new MiException("No se encontró el autor"));
                libro.setAutor(autor);

                Editorial editorial = editorialRepositorio.findById(libroModificarDTO.getEditorialId())
                        .orElseThrow(() -> new MiException("No se encontró la editorial"));
                libro.setEditorial(editorial);

                if (libroModificarDTO.getImagen() != null && !libroModificarDTO.getImagen().isEmpty()) {
                    try {
                        Imagen imagen = imagenServicio.guardar(libroModificarDTO.getImagen());
                        libro.setImagen(imagen);
                    } catch (Exception e) {
                        throw new MiException("Error al guardar la imagen: " + e.getMessage());
                    }
                }

                libroRepositorio.save(libro);
            }
        } else {
            throw new MiException("No se encontró el libro con el ISBN: " + libroModificarDTO.getIsbnOriginal());
        }
    }

    @Transactional
    public void eliminarLibro(Long isbnLibro) {

        Optional<Libro> respuesta = libroRepositorio.findById(isbnLibro);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            libroRepositorio.delete(libro);
        } else {
            throw new MiException("No se encontró el Libro con ISBN: " + isbnLibro);
        }
    }

    @Transactional
    public void darDeBajaLibro(Long isbnLibro) {

        Optional<Libro> respuesta = libroRepositorio.findById(isbnLibro);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            libro.setLibroActivo(false);
        } else {
            throw new MiException("No se encontró el libro con ISBN: " + isbnLibro);
        }
    }

    @Transactional
    public void darDeAltaLibro(Long isbnLibro) {

        Optional<Libro> respuesta = libroRepositorio.findById(isbnLibro);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            libro.setLibroActivo(true);
        } else {
            throw new MiException("No se encontró el libro con ISBN: " + isbnLibro);
        }
    }

}
