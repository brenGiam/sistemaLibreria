package com.example.libreriaapi.servicios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.libreriaapi.entidades.Editorial;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.EditorialCreateDTO;
import com.example.libreriaapi.modelos.EditorialListarDTO;
import com.example.libreriaapi.modelos.EditorialModificarDTO;
import com.example.libreriaapi.repositorios.EditorialRepositorio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EditorialServicio {

    private final EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearEditorial(EditorialCreateDTO editorialCreateDTO) {

        Editorial editorial = new Editorial();
        editorial.setNombre(editorialCreateDTO.getNombre());
        editorial.setEditorialActiva(editorialCreateDTO.isEditorialActiva());

        editorialRepositorio.save(editorial);
    }

    @Transactional(readOnly = true)
    public Page<EditorialListarDTO> listarEditoriales(Pageable pageable) {
        return editorialRepositorio.encontrarTodos(pageable);
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarTodasLasEditoriales() {
        return editorialRepositorio.findAll();
    }

    public Page<EditorialListarDTO> buscarPorNombre(String query, Pageable pageable) {
        return editorialRepositorio.buscarPorNombre(query, pageable);
    }

    @Transactional(readOnly = true)
    public Editorial obtenerEditorial(UUID id) {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new MiException("No se encontró la editorial con el id: " + id);
        }
    }

    @Transactional
    public void modificarEditorial(EditorialModificarDTO editorialModificarDTO) {

        Optional<Editorial> respuesta = editorialRepositorio.findById(editorialModificarDTO.getId());
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setNombre(editorialModificarDTO.getNombre());
            editorial.setEditorialActiva(true);
            editorialRepositorio.save(editorial);
        } else {
            throw new MiException("No se encontró la editorial con ID: " + editorialModificarDTO.getId());
        }
    }

    @Transactional
    public void eliminarEditorial(UUID idEditorial) {

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            editorialRepositorio.delete(editorial);
        } else {
            throw new MiException("No se encontró la editorial con ID: " + idEditorial);
        }
    }

    @Transactional
    public void darDeBajaEditorial(UUID idEditorial) {

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            editorial.setEditorialActiva(false);
        } else {
            throw new MiException("No se encontró la editorial con ID: " + idEditorial);
        }
    }

    @Transactional
    public void darDeAltaEditorial(UUID idEditorial) {

        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            editorial.setEditorialActiva(true);
        } else {
            throw new MiException("No se encontró la editorial con ID: " + idEditorial);
        }
    }
}
