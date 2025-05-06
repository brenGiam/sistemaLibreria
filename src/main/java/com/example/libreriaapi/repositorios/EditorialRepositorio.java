package com.example.libreriaapi.repositorios;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.libreriaapi.entidades.Editorial;
import com.example.libreriaapi.modelos.EditorialListarDTO;

@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, UUID> {

    @Query("SELECT new com.example.libreriaapi.modelos.EditorialListarDTO(e.nombre, e.id) " +
            "FROM Editorial e")
    Page<EditorialListarDTO> encontrarTodos(Pageable pageable);

    @Query("SELECT new com.example.libreriaapi.modelos.EditorialListarDTO(e.nombre, e.id) FROM Editorial e " +
            "WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<EditorialListarDTO> buscarPorNombre(@Param("query") String query, Pageable pageable);

}
