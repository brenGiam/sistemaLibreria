# 📋 Sistema gestión de librerías

Aplicación desarrollada con Java y Spring Boot que permite gestionar librerías. Esta app permite registrar, gestionar y autenticar usuarios, así como manejar libros, editoriales y autores.


## 🚀 Funcionalidades principales

- [x] Registro e inicio de sesión de usuarios
- [x] Gestión de libros, editoriales y autores (crear, leer, actualizar, eliminar)
- [x] Validación de datos
- [x] Conexión a base de datos MySQL

## 🛠️ Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- Postman (para pruebas)
- Thymeleaf

## ⚙️ Cómo correr el proyecto

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/brenGiam/sistemaLibreria

2. Importalo en tu IDE (IntelliJ, Eclipse, etc.)

3. Copiá el archivo application-example.properties y renombralo a application.properties. Luego, completalo con tus datos de conexión a la base de datos y otras configuraciones necesarias.

4. Ejecutá la clase principal de Spring Boot (SistemaLibreriaApplication.java) para poner en marcha el proyecto.

## 🧪 Pruebas
Se realizaron pruebas con Postman para verificar el correcto funcionamiento de los endpoints. A continuación, se describen algunos de los endpoints principales de la API.


## 📦 Endpoints principales

| Método | Endpoint                    | Descripción                          |
|--------|-----------------------------|--------------------------------------|
| POST   | /registro                   | Registrar un nuevo usuario           |
| GET    | /login                       | Iniciar sesión del usuario          |
| POST   | /libro/crear                | Crear un nuevo libro                 |
| GET    | /libro/listarTodos           | Listar todos los libros              |
| PUT    | /libro/modificar/{isbn}      | Actualizar un libro existente       |
| DELETE | /libro/eliminar/{isbn}       | Eliminar un libro por ISBN          |

