package com.example.libreriaapi.servicios;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.libreriaapi.entidades.Imagen;
import com.example.libreriaapi.repositorios.ImagenRepositorio;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ImagenServicio {

    private final ImagenRepositorio imagenRepositorio;

    public Imagen guardar(MultipartFile archivo) {
        if (archivo != null) {
            try {
                Imagen imagen = new Imagen();
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return imagenRepositorio.save(imagen);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
        return null;
    }

    public Imagen actualizar(MultipartFile archivo, UUID idImagen) {
        if (archivo != null) {
            try {
                if (idImagen != null) {
                    Optional<Imagen> respuesta = imagenRepositorio.findById(idImagen);
                    if (respuesta.isPresent()) {
                        Imagen imagen = respuesta.get();
                        imagen.setMime(archivo.getContentType());
                        imagen.setNombre(archivo.getName());
                        imagen.setContenido(archivo.getBytes());
                        return imagenRepositorio.save(imagen);
                    }
                }

                Imagen nuevaImagen = new Imagen();
                nuevaImagen.setMime(archivo.getContentType());
                nuevaImagen.setNombre(archivo.getName());
                nuevaImagen.setContenido(archivo.getBytes());
                return imagenRepositorio.save(nuevaImagen);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Imagen> listarTodos() {
        return imagenRepositorio.findAll();
    }

    @Transactional
    public Imagen duplicar(Imagen imagenOriginal) {
        if (imagenOriginal == null) {
            return null;
        }

        Imagen nuevaImagen = new Imagen();
        nuevaImagen.setMime(imagenOriginal.getMime());
        nuevaImagen.setNombre(imagenOriginal.getNombre());

        // Copiar el contenido del archivo
        byte[] contenidoCopiado = Arrays.copyOf(imagenOriginal.getContenido(), imagenOriginal.getContenido().length);
        nuevaImagen.setContenido(contenidoCopiado);

        return imagenRepositorio.save(nuevaImagen);
    }
}
