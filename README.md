# Backend veterinario

## Ejecutar localmente

Por defecto, la aplicación usa H2 en memoria:

```bash
./mvnw spring-boot:run
```

La API REST está bajo `http://localhost:8080/api`. Los endpoints CRUD
(`GET`, `POST`, `PUT` y `DELETE`) son:

`/users`, `/roles`, `/pets`, `/appointments`, `/appointment-medicines`,
`/attentions`, `/medicines`, `/medicine-categories`, `/services`,
`/service-categories` y `/reports`.

Las relaciones se asignan con el ID relacionado. Ejemplo para crear un rol:

```bash
curl -X POST http://localhost:8080/api/roles \
  -H 'Content-Type: application/json' \
  -d '{"name":"veterinario"}'
```

## Autenticación

`POST /api/auth/register` recibe `username`, `email` y `password`.
`POST /api/auth/login` recibe `identifier` (usuario o correo) y `password`.
Ambos devuelven un JWT. El resto de los endpoints requiere:

```json
{
  "username": "usuario",
  "email": "usuario@example.com",
  "password": "una-clave-segura"
}
```

```http
Authorization: Bearer <accessToken>
```

Las contraseñas se guardan con BCrypt y nunca se incluyen en respuestas.

Las peticiones se registran en el log con método, ruta, código de respuesta y
duración. CORS está habilitado para los frontends locales en los puertos 3000,
4200 y 5173.

## PostgreSQL

La conexión PostgreSQL se configura en
[`application-postgres.properties`](src/main/resources/application-postgres.properties).
Define las variables de entorno y activa el perfil `postgres`:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=backend
export DB_USERNAME=postgres
export DB_PASSWORD='tu-clave'
export JWT_SECRET='genera-un-secreto-aleatorio-de-al-menos-32-bytes'
export SPRING_PROFILES_ACTIVE=postgres
./mvnw spring-boot:run
```

La aplicación crea/actualiza el esquema JPA automáticamente (`ddl-auto=update`).
`JWT_SECRET` debe tener al menos 32 bytes; no uses la clave de desarrollo
predeterminada en producción.
