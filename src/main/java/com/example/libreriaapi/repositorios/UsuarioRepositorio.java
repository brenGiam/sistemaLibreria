package com.example.libreriaapi.repositorios;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.libreriaapi.entidades.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u " +
            "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) ")
    Page<Usuario> buscarPorNombreMail(@Param("query") String query, Pageable pageable);
}
