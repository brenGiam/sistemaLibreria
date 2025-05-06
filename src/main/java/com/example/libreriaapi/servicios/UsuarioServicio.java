package com.example.libreriaapi.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.example.libreriaapi.entidades.Imagen;
import com.example.libreriaapi.entidades.Usuario;
import com.example.libreriaapi.enumeraciones.Rol;
import com.example.libreriaapi.excepciones.MiException;
import com.example.libreriaapi.repositorios.UsuarioRepositorio;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final ImagenServicio imagenServicio;

    @Transactional
    public void registrarUsuario(MultipartFile archivo, String email, String nombre, String apellido, String pass1,
            String pass2)
            throws MiException {

        validar(nombre, apellido, email, pass1, pass2);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(pass1));
        usuario.setRol(Rol.USER);

        Imagen imagen = null;
        if (archivo != null && !archivo.isEmpty()) {
            imagen = imagenServicio.guardar(archivo);
        }
        usuario.setImagen(imagen);
        usuarioRepositorio.save(usuario);
    }

    public Page<Usuario> listarUsuarios(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable);
    }

    public Page<Usuario> buscarPorNombreMail(String query, Pageable pageable) {
        return usuarioRepositorio.buscarPorNombreMail(query, pageable);
    }

    @Transactional
    public void cambiarRol(UUID id) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            if (usuario.getRol().equals(Rol.USER)) {
                usuario.setRol(Rol.ADMIN);
            } else if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USER);
            }
        }
    }

    @Transactional
    public void modificarUsuario(MultipartFile archivo, UUID idUsuario, String email, String nombre, String apellido,
            String password,
            String password2) {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isEmpty()) {
            throw new MiException("El usuario especificado no existe.");
        }

        Usuario usuario = respuesta.get();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));

        if (archivo != null && !archivo.isEmpty()) {
            UUID idImagen = usuario.getImagen() != null ? usuario.getImagen().getId() : null;
            Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
            usuario.setImagen(imagen);
        }

        usuarioRepositorio.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(UUID id) {
        return usuarioRepositorio.getReferenceById(id);
    }

    @Transactional
    public void eliminarUsuario(UUID id) throws MiException {
        try {
            usuarioRepositorio.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MiException("El usuario no existe o ya fue eliminado");
        }
    }

    private void validar(String nombre, String apellido, String email, String password, String password2)
            throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("El nombre no puede ser nulo o estar vacío");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MiException("El apellido no puede ser nulo o estar vacío");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("El email no puede ser nulo o estar vacío");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
        if (usuarioRepositorio.buscarPorEmail(email) != null) {
            throw new MiException("El email ya está registrado en el sistema");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }

    }

}
