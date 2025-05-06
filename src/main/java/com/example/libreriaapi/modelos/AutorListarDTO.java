package com.example.libreriaapi.modelos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutorListarDTO {
    private String nombre;
    private UUID id;
}
