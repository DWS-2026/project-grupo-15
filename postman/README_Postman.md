# 📮 Colección Postman - TitanGym API

Esta carpeta contiene una colección completa de Postman con todos los endpoints de la API REST de TitanGym.

## 📋 Contenido

### 2. **TitanGym_Environment.postman_environment.json**
Archivo de entorno con las variables necesarias:
- `base_url`: https://localhost:8443
- `api_version`: v1
- `token`: (se debe completar después de login)
- `user_id`: 1
- `class_id`: 1
- `service_id`: 1
- `review_id`: 1
- `image_id`: 1

### 1. **TitanGym_API_Complete.postman_collection.json**
Colección completa con todos los endpoints organizados en categorías:
- 🔐 **Autenticación** (3 endpoints)
  - Registrar nuevo usuario
  - Login
  - Logout

- 👤 **Usuarios** (8 endpoints)
  - Obtener todos los usuarios
  - Crear nuevo usuario
  - Obtener mi perfil
  - Obtener usuario por ID
  - Actualizar usuario
  - Eliminar usuario
  - Subir foto de perfil
  - Obtener documento de usuario

- 🛎️ **Servicios** (7 endpoints)
  - Obtener todos los servicios
  - Crear nuevo servicio
  - Obtener servicio por ID
  - Actualizar servicio
  - Eliminar servicio
  - Subir imagen de servicio
  - **Obtener usuarios apuntados a un servicio** ✨ NUEVO

- 🏋️ **Clases** (8 endpoints)
  - Obtener todas las clases
  - Crear nueva clase
  - Obtener clase por ID
  - Actualizar clase
  - Eliminar clase
  - Obtener reseñas de una clase
  - Obtener asistentes de una clase
  - Subir imagen de clase

- ⭐ **Reseñas** (5 endpoints)
  - Obtener todas las reseñas
  - Crear nueva reseña
  - Obtener reseña por ID
  - Actualizar reseña
  - Eliminar reseña

- 📄 **Documentos** (3 endpoints)
  - Obtener mi documento
  - Subir documento
  - Eliminar documento

- 🖼️ **Imágenes** (1 endpoint)
  - Descargar imagen por ID

## 🚀 Cómo importar en Postman

### Paso 1: Importar la colección
1. Abre Postman
2. Click en "Import" (esquina superior izquierda)
3. Selecciona el archivo `TitanGym_API_Complete.postman_collection.json`
4. Click en "Import"

### Paso 2: Importar el entorno
1. Click en "Import" nuevamente
2. Selecciona el archivo `TitanGym_Environment.postman_environment.json`
3. Click en "Import"

### Paso 3: Seleccionar el entorno
1. En la esquina superior derecha, selecciona el entorno "TitanGym - Desarrollo"

## 🔑 Autenticación

### 1. Registrarse (si no tienes cuenta)
- Usa el endpoint: `POST /api/v1/auth/register`
- Rellena el body con tus datos:
  ```json
  {
    "firstName": "Tu Nombre",
    "lastName": "Tu Apellido",
    "email": "tu@email.com",
    "password": "tuPassword123"
  }
  ```

### 2. Hacer Login
- Usa el endpoint: `POST /api/v1/auth/login`
- Rellena el body:
  ```json
  {
    "email": "tu@email.com",
    "password": "tuPassword123"
  }
  ```
- Copia el token de la respuesta

### 3. Guardar el token
- En Postman, ve a "Environment" 
- Selecciona "TitanGym - Desarrollo"
- Pega el token en la variable `token`
- Click en "Save" o presiona Ctrl+S

## 📝 Ejemplos de uso

### Ejemplo 1: Obtener mi perfil
```
GET /api/v1/users/me
Headers:
  Authorization: Bearer {{token}}
```

### Ejemplo 2: Crear una nueva clase
```
POST /api/v1/classes
Headers:
  Authorization: Bearer {{token}}
  Content-Type: application/json

Body:
{
  "name": "Pilates Matutino",
  "description": "Clase de pilates para fortalecer el core",
  "schedule": "Lunes, Miércoles y Viernes de 08:00 a 09:00"
}
```

### Ejemplo 3: Subir una imagen de perfil
```
POST /api/v1/users/{{user_id}}/image
Headers:
  Authorization: Bearer {{token}}

Body (form-data):
  imageFile: [selecciona tu archivo de imagen]
```

## ⚙️ Configuración importante

### SSL/TLS en localhost
Como la API usa HTTPS con certificado autofirmado:
1. En Postman, ve a Settings (engranaje arriba a la derecha)
2. Ve a "Certificates"
3. Desactiva "SSL certificate verification" para desarrollo

Alternatively:
1. Ir a Settings → General
2. Deshabilitar "SSL certificate verification"

## 🎯 Variables disponibles

Puedes usar estas variables en cualquier request:
- `{{base_url}}` → https://localhost:8443
- `{{api_version}}` → v1
- `{{token}}` → Tu token JWT
- `{{user_id}}` → ID del usuario (cambiar según necesites)
- `{{class_id}}` → ID de la clase (cambiar según necesites)
- `{{service_id}}` → ID del servicio (cambiar según necesites)
- `{{review_id}}` → ID de la reseña (cambiar según necesites)
- `{{image_id}}` → ID de la imagen (cambiar según necesites)

## 📚 Notas importantes

- La mayoría de endpoints requieren autenticación (token JWT)
- Solo autenticación y registro NO requieren token
- Todos los IDs de ejemplo (1, 2, 3, etc.) deben ser reemplazados con valores reales
- Los horarios (schedule) pueden tener cualquier formato de texto
- Las imágenes deben ser archivos válidos (JPG, PNG, etc.)
- Los documentos deben ser archivos válidos (PDF, DOCX, etc.)

## 🐛 Troubleshooting

### Error: "SSL certificate problem"
→ Deshabilita SSL certificate verification en Postman Settings

### Error: "401 Unauthorized"
→ Verifica que hayas iniciado sesión y copiar correctamente el token

### Error: "404 Not Found"
→ Verifica que el ID existe en la base de datos

### Error: "400 Bad Request"
→ Revisa el formato del JSON enviado en el body

## 📞 Soporte

Si tienes problemas:
1. Verifica que el servidor esté corriendo en `https://localhost:8443`
2. Comprueba que los datos del environment estén correctos
3. Asegúrate de tener el token válido (no vencido)
4. Revisa la consola del servidor para ver más detalles

---

**Última actualización**: Mayo 7, 2026
