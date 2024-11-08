# Proyecto backend springboot prueba técnica



## Funcionamiento

El proyecto es un backend desarrollado en Springboot que permite registrar y autenticar usuarios, listar clientes y subir un archivo excel con clientes.
El motor de base de datos es PostgreSQL el cual se ejecuta en un contenedor de Docker cuando se ejecuta el docker-compose.

## Endpoints

`POST http://localhost:8080/api/auth/register` - Registro de usuario (Genera un token de autenticación y un token de refresco)

`POST http://localhost:8080/api/auth/authenticate` - Login de usuario (Genera un token de autenticación y un token de refresco)

`GET http://localhost:8080/api/clients` - Listar clientes (requiere autenticación)

`POST http://localhost:8080/api/clients/upload` - Subir excel de clientes (requiere autenticación)

## Requisitos

- Java 17
- Maven
- Docker

## Ejecución

Ejecuta el proyecto en local con `mvn spring-boot:run` (Necesita ejecutar el docker-compose para levantar la base de datos)

## Test

Ejecuta los test con `mvn test`

## Build del proyecto

Construye el jar del proyecto con `mvn clean package`

## Docker

Para ejecutar el proyecto en un contenedor de Docker, primero debes construir el jar del proyecto.

Luego ejecuta el docker-compose con `docker-compose up` o `docker-compose up -d` para ejecutarlo en segundo plano.

Está configurado para que se comunique con la base de datos en el contenedor de Docker.

Para eliminar el contenedor y los volumenes ejecuta `docker-compose down -v`

## Pendientes

- Agregar excepciones al controller advice



