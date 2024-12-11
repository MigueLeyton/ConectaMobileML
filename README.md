# ConectaMobileML

## 📱 Descripción

**ConectaMobileML** es una aplicación móvil Android desarrollada en **Android Studio** que permite a los usuarios registrarse, gestionar su perfil, conectarse con otros usuarios y enviar mensajes. Esta aplicación utiliza **Firebase** para el almacenamiento de datos y la autenticación, y permite almacenar fotos de perfil, realizar operaciones CRUD completas (Crear, Leer, Actualizar, Eliminar) sobre los datos del usuario, y buscar usuarios por su **nombre**, **apellido** y **correo electrónico**.

En el futuro, se integrará un sistema de mensajería completo para que los usuarios puedan comunicarse entre sí de manera eficiente.

## 🌟 Características

- **Autenticación con Firebase**: Login y registro de usuarios usando el sistema de autenticación de Firebase.
- **Gestión de Perfil**: Los usuarios pueden crear, editar y eliminar su perfil, incluyendo la foto de perfil.
- **Búsqueda de Usuarios**: Los usuarios pueden buscar a otros usuarios por su **nombre**, **apellido** o **correo electrónico**.
- **Almacenamiento en Firebase**: Todos los datos de los usuarios se almacenan en **Firebase Firestore** y las fotos de perfil en **Firebase Storage**.
- **Mensajería (en desarrollo)**: Implementación futura de un sistema de mensajería para que los usuarios puedan intercambiar mensajes en tiempo real.

## 🛠️ Tecnologías Utilizadas

- **Android Studio** (IDE para desarrollo)
- **Java** (Lenguaje de programación principal)
- **Firebase**:
  - **Firebase Authentication** para el login y registro de usuarios.
  - **Firebase Firestore** para almacenar los datos de los usuarios.
  - **Firebase Storage** para almacenar las fotos de perfil.
- **RecyclerView** para mostrar la lista de usuarios y sus datos.
- **SearchView** para realizar búsquedas en la lista de usuarios.


---

## 🚀 Uso

### Login/Registro de Usuario

- Al abrir la aplicación, se pedirá a los usuarios que inicien sesión o se registren.
- Los nuevos usuarios pueden crear una cuenta usando su **correo electrónico** y **contraseña**.
- Los usuarios registrados pueden iniciar sesión con sus credenciales.

### Perfil de Usuario

- Los usuarios pueden ver y editar su perfil, incluyendo su **nombre**, **apellido** y **foto de perfil**.
- La foto de perfil se puede actualizar desde el almacenamiento local.

### Buscar Usuarios

- Los usuarios pueden buscar a otros usuarios por su **nombre**, **apellido** o **correo electrónico** utilizando un campo de búsqueda integrado.

### Mensajería (Próximamente)

- El sistema de mensajería estará disponible en una próxima versión.
- Los usuarios podrán enviarse mensajes entre sí en tiempo real.

---

## 📋 Roadmap

- ✅ **Login/Registro de Usuario** (Completo)
- ✅ **Gestión de Perfil** (Completo)
- ✅ **Búsqueda de Usuarios** (Completo)
- 🚧 **Sistema de Mensajería** (En desarrollo)

---

## 🔒 Licencia

Este proyecto está bajo la **Licencia MIT**. Consulta el archivo [LICENSE](LICENSE) para más detalles.

