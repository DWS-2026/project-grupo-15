# TitanGym

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Manuel García Muñoz | m.garciamu.2024@alumnos.urjc.es | manugrmz |
| Daniel Puga Blanco | d.puga.2024@alumnos.urjc.es | D_Puga |
| Genshen Lin | g.lin.2024@alumnos.urjc.es | gln16 |
| Héctor Bonilla Labraca | h.bonilla.2024@alumnos.urjc.es | hectorbn |

---

## 🎭 **Preparación: Definición del Proyecto**

### **Descripción del Tema**
La aplicación es un sistema web para la gestión de un gimnasio. Permite a los usuarios registrarse, consultar clases disponibles, reservar actividades y gestionar su perfil personal.
El sector al que pertenece es el fitness y bienestar, y aporta valor al usuario facilitando la organización de sus entrenamientos, la reserva de clases y el acceso a información relevante del gimnasio.
Para los administradores, la aplicación ofrece herramientas para gestionar usuarios, clases y actividades de forma centralizada.

### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **[Entidad 1]**: Usuario
2. **[Entidad 2]**: Clase
3. **[Entidad 3]**: Actividad/Servicio
4. **[Entidad 4]**: Review

**Relaciones entre entidades:**
- Usuario-Review: un usuario puede tener múltiples reviews. RELACIÓN 1:N.
- Clase-Review: una clase puede tener múltiples reviews, pero cada review tiene una única clase. RELACIÓN 1:N.
- Actividad-Review: una actividad puede tener múltiples reviews. RELACIÓN 1:N
- Usuario-Clase: un usuario puede apuntarse a muchas clases y una clase puede tener muchos usuarios. RELACIÓN N:M.
- Usuario-Actividad: RELACION N:M.

### **Permisos de los Usuarios**
Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

* **Usuario Anónimo**: 
  - Permisos: Ver la página principal, consultar listado de clases, consultar el listado de actividades, acceder el formulario de registro, acceder al login.
  - No es dueño de ninguna entidad

* **Usuario Registrado**: 
  - Permisos: Iniciar y cerrar sesión, editar su perfil, ver el detalle de clases y actividades, reservar clases y actividades, cancelar sus propias actividades.
  - Es dueño de: su perfil y sus reviews.

* **Administrador**: 
  - Permisos: Gestionar todos los usuarios, crear, editar y eliminar clases y actividades, ver todas las reservas, y modificar datos generales del gimnasio.
  - Es dueño de: todas las clases, reviews, actividades, y usuarios(a nivel gestión).

### **Imágenes**
Indicar qué entidades tendrán asociadas una o varias imágenes:

- **[Entidad con imágenes 1]**: Usuario--> Una imagen de perfil(avatar).
- **[Entidad con imágenes 2]**: Clase--> Una imagen representativa de la clase(por ejemplo de yoga, zumba..).
- **[Entidad con imágenes 3]**: Actividad/Servicio--> Imagen ilustrativa del servicio(nutrición, fisitoterapia...).

---

## 🛠 **Práctica 1: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/xOCpoDu5FGM)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/diagrama.png)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Inicio**
![Página Principal](images/inicio.png)

> ["Página de inicio que muestra opción a registrarse o en su defecto a iniciar sesión, podemos ver las múltiples opciones de arriba a al derecha siendo la más destacada la de MI PERFIL en la que si se estuviera iniciado sesión podría ver sus datos personales"]

#### **2.Las clases / Inicio**
![Nuestras clases](images/imagen2.png)

> ["Bajando en el index, encontramos la lista de clases activas que pueden visionar cualquier usuario."]

#### **3.Los servicios / Inicio**
![Nuestros servicios](images/imagen3.png)

> ["Bajando aún más encontramos los servicios disponibles"]

#### **4.Los servicios(precios) / Precio**
![Precios servicios](images/imagen4.png)

> ["En QUE OFRECEMOS(feature.html) encontramos los precios disponibles de nuestros servicios activos(actividades no ya que no requieren de otra sucripción)"]


#### **5.Perfil inicio/ Perfil**
![Inicio Perfil](images/perfil.png)

> ["Aquí encontramos en el botón de mi perfil(ya iniciado sesión) los datos de su cuenta pudiendolos editar, además de ver sus clases y servicios en propiedad además de sus reviews"]

#### **6.Perfil Servicios/ Perfil**
![Servicios suscritos](images/perfil3.png)

> ["Podemos desde aquí suscribirnos a servicios o una vez ya sucritos eliminarlosde suscripción "]

#### **7.Perfil Clases/ Perfil**
![Clases apuntados](images/perfil3.png)

> ["De igual manera que con los servicios hacemos con las clases "]

#### **8.Perfil Reviews/ Perfil**
![Reviews del usuario](images/perfil4.png)

> ["Desde aquí podemos ver nuestras reviews hechas y añadir más(solo pueden añadir susuarios registrados) "]

#### **9.Admin Inicio/ Admin**
![Inicio](images/admin1.png)

> ["Es el inicio de la página del Admin, en su lateral con todas las opciones posibles "]

#### **10.Admin Miembros/ Admin**
![Miembros](images/admin2.png)

> ["Aquí el admin puede eliminar, editar y ver el perfil de los miembros registrados del gimnasio "]

#### **11.Admin Clases/ Admin**
![Clases](images/admin3.png)

> ["Aquí puede gestionar las clases disponibles además de añadir más "]

#### **12.Admin Servicios/ Admin**
![Servicios](images/admin4.png)

> [Descripción breve: Ej: "Aquí puede gestionar los servicios disponibles además de añadir más "]

#### **13.Admin Panel/ Admin**
![Panel](images/admin5.png)

> ["Aquí puede editar temas más centrados con su perfil y datos del gimnasio como horario, número etc.. "]

#### **14.Admin Panel/ Admin**
![Reviews](images/admin6.png)

> ["Muestra en esta pantalla las reviews de la gente, pudiendo eliminar en caso de que no sea correcta "]

#### **15.Contacto/ Contacto**
![Contactos](images/contacto.png)

> ["En esta pantalla podemos encontrar los contactos y ubicación en el mapa para ubicar el gimnasio"]

#### **16.Registro/ Registro**
![Resgistro](images/login.png)

> ["Podemos ver la pantalla de registro con todas sus funcionalidades"]

#### **17.Inicar Sesión/ Iniciar Sesión**
![Sesión](images/iniciosesion.png)

> ["Inicio de sesión con su usuario y contraseña"]

#### **18.Pago Inicio/ Pago**
![Pago](images/pago.png)

> ["Después del registro, nos lleva al pago, cuyo monto depende de los extras escogidos"]

#### **19.Pago Éxito/ Pago**
![Pago](images/pago2.png)

> ["Tras pagar sale la pantalla de éxito y ya tenemos el perfil registrado listo para su uso"]

  #### **20.Reciperación/ Contraseña**
![Contraseña](images/recu.png)

> ["Pantalla para recuperación de contraseña"]


### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - Manuel García Muñoz**

Principalmente la realización del panel admin y todas sus funcionalidades, además de ayudar en otras partes de la web.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [PANEL ADMIN](https://github.com/DWS-2026/project-grupo-15/commit/df2ecee25a182267b187729edb4dd67e7b3d2cc2)  | [admin.html](https://github.com/DWS-2026/project-grupo-15/blob/main/admin.html)   |
|2| [LOGIN Y MAS](https://github.com/DWS-2026/project-grupo-15/commit/52ca49ab0c837b58dc52f49f3a9d10c749454fe2)  | [login.html](https://github.com/DWS-2026/project-grupo-15/blob/main/login.html)   |
|3| [ADMIN LISTA USUARIOS](https://github.com/DWS-2026/project-grupo-15/commit/1c731c0b4a21cf28c1f12b6ad675684c77cde54d)  | [admin-usuarios.html](https://github.com/DWS-2026/project-grupo-15/blob/main/admin-usuarios.html)   |
|4| [SELECCION PLANTILLA](https://github.com/DWS-2026/project-grupo-15/commit/107028bcec4f13066262b1b0b6fd37b004fbf1aa)  | [index.html](https://github.com/DWS-2026/project-grupo-15/blob/main/index.html)   |
|5| [CREACION PERFIL USUARIO Y DEMAS](https://github.com/DWS-2026/project-grupo-15/commit/54ab67c3ca03cb19b94bdf204750107fe5ff0830#diff-3985f9cbed74f52f435a1951d84ebda87755c78fd23f513c6211f6b8f6fc9698)  | [perfil.html](https://github.com/DWS-2026/project-grupo-15/blob/main/perfil.html)   |

---

#### **Alumno 2 - Daniel Puga Blanco**

Encargado de la creación del login y del registro, además de la modificación de la página de clases y de la de contacto, además de pequeñas correcciones en el codigo de otras páginas.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [PÁGINA LOGIN](https://github.com/DWS-2026/project-grupo-15/commit/89a75f14019687a329acdf8a7a3a2a55470d6c44)  | [login.html](URL_archivo_1)   |
|2| [AÑADIR PÁGINA REGISTRO](https://github.com/DWS-2026/project-grupo-15/commit/8cf1c6a263d9799ec468a2cf1e1060134a48e2b6)  | [registro.html](https://github.com/DWS-2026/project-grupo-15/blob/8cf1c6a263d9799ec468a2cf1e1060134a48e2b6/registro.html)   |
|3| [PÁGINA CLASES](https://github.com/DWS-2026/project-grupo-15/commit/e34a23f51a17a902e90002fe79c8b65c323a9d4a)  | [class.html](https://github.com/DWS-2026/project-grupo-15/blob/e34a23f51a17a902e90002fe79c8b65c323a9d4a/class.html)   |
|4| [PÁGINA CONTACTO](https://github.com/DWS-2026/project-grupo-15/commit/ee4ee219ba1f62133cd0b604d263092dc554c58b)  | [contact.hmtl](https://github.com/DWS-2026/project-grupo-15/blob/ee4ee219ba1f62133cd0b604d263092dc554c58b/contact.html)   |
|5| [PÁGINA RECUPERACIÓN CONTRASEÑA](https://github.com/DWS-2026/project-grupo-15/commit/9552ed233307217b29165e2f88735d26c23ba6aa)  | [recuperar-contraseña.html](https://github.com/DWS-2026/project-grupo-15/blob/9552ed233307217b29165e2f88735d26c23ba6aa/recuperar-contrase%C3%B1a.html)   |
---

#### **Alumno 3 - [Genshen Lin]**

Realización de la página del usuario en cuestión, mejora de la página de registro, pantalla de pago, labores corrección de errores y mejoras.

| Nº | Commits | Files |
|:---:|:---|:---|
|1| [Página de pago](https://github.com/DWS-2026/project-grupo-15/commit/85ebed01d4cffe71488c1a1d0cc467319818b467) | [payment.html](https://github.com/DWS-2026/project-grupo-15/blob/main/payment.html) |
|2| [Listado de clases que se puede apuntar el usuario](https://github.com/DWS-2026/project-grupo-15/commit/c8770b6731ba45a184b3f18331b94ebd0df0eade#diff-0573a7817d0b88035347e89a4761625b0e87c6e59fc1b508e687f63c89ccfeb1) | [clases-listado.html](https://github.com/DWS-2026/project-grupo-15/blob/main/clases-listado.html) |
|3| [Listado de servicios que se puede suscribir el usuario](https://github.com/DWS-2026/project-grupo-15/commit/c8770b6731ba45a184b3f18331b94ebd0df0eade#diff-0573a7817d0b88035347e89a4761625b0e87c6e59fc1b508e687f63c89ccfeb1) | [servicios-listado.html](https://github.com/DWS-2026/project-grupo-15/blob/main/servicios-listado.html) |
|4| [Actualización página de registro con funciones extra y visuales](https://github.com/DWS-2026/project-grupo-15/commit/c9f8aa50daf24916abfc156e1667c6f2d09ecdbd#diff-0573a7817d0b88035347e89a4761625b0e87c6e59fc1b508e687f63c89ccfeb1) | [registro.html](https://github.com/DWS-2026/project-grupo-15/blob/main/registro.html) |
|5| [Pantalla éxito pago](https://github.com/DWS-2026/project-grupo-15/commit/16c2304b953fdeca10111ee00a95da4bb1b28405#diff-1a9ac281340abc5984f9abaa141069fce1d9348a8883bcdef07325518bb59149) | [successful.html](https://github.com/DWS-2026/project-grupo-15/blob/main/successful.html) |

---

#### **Alumno 4 - [Héctor Bonilla]**

Encargado de la realización del apartado de reviews del usuario y reviews en general y del apartado de que ofrecemos en el feature y traduxxión.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [ACTUALIZAR PÁGINA QUÉ OFRECEMOS (SERVICIOS Y PRECIOS)](URL_commit_1)  | [feature.html](URL_archivo_1)   |
|2| [TRADUCIR HEADER Y TEXTOS DE FEATURE A ESPAÑOL](URL_commit_2)  | [feature.html](URL_archivo_2)   |
|3| [TRADUCIR FOOTER A ESPAÑOL Y ARREGLAR ENLACES](URL_commit_3)  | [feature.html](URL_archivo_3)   |
|4| [AÑADIR SECCIÓN REVIEWS EN MENÚ DE PERFIL](URL_commit_4)  | [reviews.html](URL_archivo_4)   |
|5| [CREAR PÁGINA REVIEWS (LISTADO + FORMULARIO)](URL_commit_5)  | [reviews.html](URL_archivo_5)   |

---

## 🛠 **Práctica 2: Web con HTML generado en servidor**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/kIxQtGaD6js)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

Solo si han cambiado.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/DWS-2026/project-grupo-15.git
   cd project-grupo-15
   ```

2. **AQUÍ INDICAR LO SIGUIENTES PASOS**

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin@titangym.com`, contraseña: `admin123`
- **Usuario Registrado**: usuario: `paco@gmail.com`, contraseña: `1234`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](profile_images/diagramanuevo.png)

> [Descripción opcional: Ej: "El diagrama muestra las 4 entidades principales: Usuario, Review, Clase y Servicio, con sus respectivos atributos y relaciones 1:N y N:M."]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](profile_images/clases-diagrama.png)


### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Manuel García Muñoz]**

[He realizado tareas como la configuración de la base de datos, y su diagrama, además de tareas del panel del admin]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [DIAGRAMA BASE DE DATOS](https://github.com/DWS-2026/dws-2026-project-base/commit/abb7d4102a51756ad146dde007911732150e0f33)  | [diagrambbdd.png](profile_images/diagramabbdd.png)   |
|2| [PANEL ADMIN EDICION USUARIOS](https://github.com/DWS-2026/dws-2026-project-base/commit/a3b1dddf0b0cc0bcfd9701b513a4ed00b812b542)  | [AdminController.java](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/AdminController.java)   |
|3| [BASE DE DATOS](https://github.com/DWS-2026/dws-2026-project-base/commit/e988d9981dfa0f9aac94a0c0e9f2310807d13a33)  | [Application.properties](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/resources/application.properties)   |
|4| [MANTENIMIENTO DE LA SESIÓN](https://github.com/DWS-2026/dws-2026-project-base/commit/73beb135c2becd9eadbb8a4937e1d18ec25cb660)  | [GlobalControllerAdvice.java](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/GlobalControllerAdvice.java)   |
|5| [CREACIÓN ENTIDADES](https://github.com/DWS-2026/dws-2026-project-base/commit/f6434f10d2880098231a2c5b91907262df0a4608)  | [Carpeta entidades](https://github.com/DWS-2026/project-grupo-15/tree/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/model)   |

---

#### **Alumno 2 - [Daniel Puga Blanco]**

Encargado de todo lo relacionado con la entidad clases y algunas cosas de seguridad. Ademas de ser el responsable del video

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Creacion de pagina de errores](https://github.com/DWS-2026/project-grupo-15/commit/d75dca4f5722301503740b31efe09a42df1fe8af)  | [Archivo1](URL_archivo_1)   |
|2| [CSFR](https://github.com/DWS-2026/project-grupo-15/commit/7ff1f62f2993a8267edecded04aa6b75163448e1)  | [Archivo2](https://github.com/DWS-2026/project-grupo-15/blob/7ff1f62f2993a8267edecded04aa6b75163448e1/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/SecurityConfiguration.java)   |
|3| [admin clases](https://github.com/DWS-2026/project-grupo-15/commit/feb1b86c85975cd942ef6a4659d1fe09b8774fdd)  | [Archivo3](https://github.com/DWS-2026/project-grupo-15/blob/feb1b86c85975cd942ef6a4659d1fe09b8774fdd/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/AdminController.java)   |
|4| [Class controller](https://github.com/DWS-2026/project-grupo-15/commit/ebd78572643c4cf882db4184c33648e4768199b0)  | [Archivo4](https://github.com/DWS-2026/project-grupo-15/blob/ebd78572643c4cf882db4184c33648e4768199b0/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/ClassController.java)   |
|5| [Relacion clase-usuario](https://github.com/DWS-2026/project-grupo-15/commit/58eb534f9b7d29fc6655f198217828cfdcfa6821)  | [Archivo5](https://github.com/DWS-2026/project-grupo-15/blob/58eb534f9b7d29fc6655f198217828cfdcfa6821/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/service/DataBaseInitializer.java)   |
|6| [Clases en bbdd](https://github.com/DWS-2026/project-grupo-15/commit/b6a1835c3f9e42c59f9fcd87bf9adfbe14332c23)  | [Archivo6](proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/WebController.java)   |

---

#### **Alumno 3 - [Genshen Lin]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Cambio Base de Datos Entidad Servicios y corregir](https://github.com/DWS-2026/project-grupo-15/commit/278c6c22ede8d7fb687a289d5925916493254a9f)  | [Service.Entity](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/model/ServiceEntity.java)   |
|2| [Implementación Seguridad](https://github.com/DWS-2026/project-grupo-15/commit/ee096938bbc81a72ed9ab9c9d601b781a69b3cf2)  | [SecurityConfiguration.java](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/SecurityConfiguration.java)   |
|3| [Dinámico paginas iniciales](https://github.com/DWS-2026/project-grupo-15/commit/ceba93ec53007d977fe21581a6f69af53ccd2c8e)  | [index.html] (https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/resources/templates/index.html)   |
|4| [Dinamico Admin](https://github.com/DWS-2026/project-grupo-15/commit/6b4c56dca5fc9fad014d47eb4bca35a122dadf62)  | [AdminController](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/AdminController.java)   |
|5| [Dinamico User](https://github.com/DWS-2026/project-grupo-15/commit/29d45b89140d1ccceb94624d55781e2ca64d32ee)  | [Profile Controller](https://github.com/DWS-2026/project-grupo-15/blob/main/proyecto-dws-grupo2/src/main/java/es/codeurjc/proyecto_dws_grupo2/controller/ProfileController.java)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Incorporación de una API REST a la aplicación web, análisis de vulnerabilidades y contramedidas**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](profile_images/ca68f518-6218-43c2-8523-97a6147cd70d.jfif)

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin123 |
| Usuario Registrado | user1 | user123 |
| Usuario Registrado | user2 | user123 |

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
