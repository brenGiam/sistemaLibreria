package com.example.libreriaapi.modelos;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class LibroModificarDTO {
    private Long isbnOriginal;
    private Long isbn;
    private String titulo;
    private Integer ejemplares;
    private UUID autorId;
    private UUID editorialId;
    private boolean libroActivo;
    private MultipartFile imagen;
}
