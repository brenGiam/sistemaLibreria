package com.example.libreriaapi.modelos;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class LibroCreateDTO {
    private Long isbn;
    private String titulo;
    private Integer ejemplares;
    private UUID idAutor;
    private UUID idEditorial;
    private boolean libroActivo;
    private MultipartFile imagen;
}
