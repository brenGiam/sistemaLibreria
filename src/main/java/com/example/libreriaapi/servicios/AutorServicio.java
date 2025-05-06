package com.example.libreriaapi.servicios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.libreriaapi.entidades.Autor;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.modelos.AutorCreateDTO;
import com.example.libreriaapi.modelos.AutorListarDTO;
import com.example.libreriaapi.modelos.AutorModificarDTO;
import com.example.libreriaapi.repositorios.AutorRepositorio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AutorServicio {

    private final AutorRepositorio autorRepositorio;

    @Transactional
    public void crearAutor(AutorCreateDTO autorCreateDTO) {

        Autor autor = new Autor();
        autor.setNombre(autorCreateDTO.getNombre());
        autor.setAutorActivo(autorCreateDTO.isAutorActivo());

        autorRepositorio.save(autor);
    }

    @Transactional(readOnly = true)
    public Page<AutorListarDTO> listarAutores(Pageable pageable) {
        return autorRepositorio.encontrarTodos(pageable);
    }

    @Transactional(readOnly = true)
    public List<Autor> listarTodosLosAutores() {
        return autorRepositorio.findAll();
    }

    public Page<AutorListarDTO> buscarPorNombre(String query, Pageable pageable) {
        return autorRepositorio.buscarPorNombre(query, pageable);
    }

    @Transactional(readOnly = true)
    public Autor obtenerAutor(UUID id) {
        Optional<Autor> respuesta = autorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new MiException("No se encontró el autor con el id: " + id);
        }
    }

    @Transactional
    public void modificarAutor(AutorModificarDTO autorModificarDTO) {

        Optional<Autor> respuesta = autorRepositorio.findById(autorModificarDTO.getId());
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setNombre(autorModificarDTO.getNombre());
            autor.setAutorActivo(true);
            autorRepositorio.save(autor);
        } else {
            throw new MiException("No se encontró el autor con ID: " + autorModificarDTO.getId());
        }
    }

    @Transactional
    public void eliminaAutor(UUID idAutor) {

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();

            autorRepositorio.delete(autor);
        } else {
            throw new MiException("No se encontró el autor con ID: " + idAutor);
        }
    }

    @Transactional
    public void darDeBajaAutor(UUID idAutor) {

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();

            autor.setAutorActivo(false);
        } else {
            throw new MiException("No se encontró el autor con ID: " + idAutor);
        }
    }

    @Transactional
    public void darDeAltaAutor(UUID idAutor) {

        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();

            autor.setAutorActivo(true);
        } else {
            throw new MiException("No se encontró el autor con ID: " + idAutor);
        }
    }
}
