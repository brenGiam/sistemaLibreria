package com.example.libreriaapi.modelos;

import java.util.UUID;

import lombok.Data;

@Data
public class EditorialModificarDTO {
    private UUID id;
    private String nombre;
    private boolean editorialActiva;
}
