package com.example.libreriaapi.repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.libreriaapi.entidades.Libro;
import com.example.libreriaapi.modelos.LibroListarDTO;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Long> {

        @Query("SELECT new com.example.libreriaapi.modelos.LibroListarDTO(l.titulo, l.isbn) " +
                        "FROM Libro l")
        Page<LibroListarDTO> encontrarTodos(Pageable pageable);

        @Query("SELECT new com.example.libreriaapi.modelos.LibroListarDTO(l.titulo, l.isbn) FROM Libro l " +
                        "JOIN l.autor a " +
                        "JOIN l.editorial e " +
                        "WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :query, '%')) ")
        Page<LibroListarDTO> buscarPorTituloAutorEditorial(@Param("query") String query, Pageable pageable);

}
