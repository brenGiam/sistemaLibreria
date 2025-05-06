package com.example.libreriaapi.repositorios;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.libreriaapi.entidades.Autor;
import com.example.libreriaapi.modelos.AutorListarDTO;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, UUID> {
        @Query("SELECT new com.example.libreriaapi.modelos.AutorListarDTO(a.nombre, a.id) " +
                        "FROM Autor a")
        Page<AutorListarDTO> encontrarTodos(Pageable pageable);

        @Query("SELECT new com.example.libreriaapi.modelos.AutorListarDTO(a.nombre, a.id) FROM Autor a " +
                        "WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
        Page<AutorListarDTO> buscarPorNombre(@Param("query") String query, Pageable pageable);
}
