package com.example.libreriaapi.modelos;

import java.util.UUID;

import lombok.Data;

@Data
public class AutorCreateDTO {
    private UUID id;
    private boolean autorActivo;
    private String nombre;

}
