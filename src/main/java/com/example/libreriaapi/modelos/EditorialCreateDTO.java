package com.example.libreriaapi.modelos;

import java.util.UUID;

import lombok.Data;

@Data
public class EditorialCreateDTO {
    private UUID id;
    private boolean editorialActiva;
    private String nombre;
}
