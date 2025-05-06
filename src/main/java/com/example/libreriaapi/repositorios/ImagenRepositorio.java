package com.example.libreriaapi.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.libreriaapi.entidades.Imagen;

@Repository
public interface ImagenRepositorio extends JpaRepository<Imagen, UUID> {

}
