package com.example.libreriaapi.controladores;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.libreriaapi.entidades.Libro;
import com.example.libreriaapi.entidades.Usuario;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.servicios.ImagenServicio;
import com.example.libreriaapi.servicios.LibroServicio;
import com.example.libreriaapi.servicios.UsuarioServicio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/imagen")
public class ImagenControlador {

    private final UsuarioServicio usuarioServicio;
    private final ImagenServicio imagenServicio;
    private final LibroServicio libroServicio;

    // Obtener imagen de perfil de usuario
    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> imagenUsuario(@PathVariable UUID id) {
        Usuario usuario = usuarioServicio.obtenerUsuario(id);
        byte[] imagen = usuario.getImagen().getContenido();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
    }

    // Subir imagen de perfil para un usuario
    @PostMapping("/perfil/{id}")
    public ResponseEntity<String> actualizarImagenPerfil(@PathVariable UUID id,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            imagenServicio.actualizar(archivo, id);
            return new ResponseEntity<>("Imagen actualizada exitosamente",

                    HttpStatus.OK);
        } catch (MiException ex) {
            return new ResponseEntity<>("Error al actualizar la imagen",

                    HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener imagen de libro por ISBN
    @GetMapping("/libro/{isbn}")
    public ResponseEntity<byte[]> imagenLibro(@PathVariable Long isbn) {
        Libro libro = libroServicio.obtenerLibro(isbn);
        if (libro != null && libro.getImagen() != null) {
            byte[] imagen = libro.getImagen().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Subir imagen de libro
    @PostMapping("/libro/{isbn}")
    public ResponseEntity<String> actualizarImagenLibro(@PathVariable UUID id,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            imagenServicio.actualizar(archivo, id);
            return new ResponseEntity<>("Imagen del libro actualizada exitosamente", HttpStatus.OK);
        } catch (MiException ex) {
            return new ResponseEntity<>("Error al actualizar la imagen del libro", HttpStatus.BAD_REQUEST);
        }
    }
}
