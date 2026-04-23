# RevelApp (ProyectoEvento)

Aplicación móvil Android profesional para la gestión de eventos y control de usuarios, con integración en la nube.

## Descripción

RevelApp es una solución integral para organizadores de eventos sociales y corporativos. Permite gestionar desde la cotización inicial hasta el seguimiento de pagos y registro de usuarios, todo bajo una interfaz moderna, segura y sincronizada en tiempo real.

## Novedades (Últimos Avances)

- **Sistema de Autenticación:** Integración con **Firebase Auth** para inicio de sesión y registro de usuarios.
- **Base de Datos en Tiempo Real:** Uso de **Google Cloud Firestore** para el almacenamiento de datos.
- **Flujo de Seguridad:** 
  - Pantalla de Login con validación de credenciales.
  - Pantalla de Registro con aviso de éxito (Snackbar) y redirección automática.
  - Función de Cerrar Sesión desde la pantalla principal.
- **Gestión de Usuarios (CRUD):** Sección administrativa para dar de alta, listar, editar y eliminar usuarios del sistema.
- **UI Unificada:** Estética profesional basada en el color de marca `#07505A` y tipografía moderna.

## Tecnologías

- **Lenguaje:** Kotlin 2.1.0
- **UI:** Jetpack Compose + Material Design 3
- **Backend:** Firebase (Authentication & Firestore)
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Manejo de Estado:** StateFlow + SharedFlow para eventos de un solo uso
- **Build:** Gradle 8.10.2
- **SDK Objetivo:** API 35 (Android 15)

## Estructura del proyecto

```
app/src/main/java/com/itson/proyectoevento/
├── MainActivity.kt          # Orquestador de navegación y flujo principal
├── data/
│   └── model/               # Modelos de datos (Evento, Usuario, Pago)
└── ui/
    ├── login/               # Pantallas de Login y Registro (Firebase)
    ├── inicio/              # Dashboard principal y lista de eventos
    ├── usuarios/            # CRUD completo de gestión de usuarios
    ├── newEvent/            # Formulario de creación de eventos
    ├── theme/               # Configuración estética (Color, Theme, Type)
    └── common/              # Componentes reutilizables
```

## Funcionalidades Clave

### Seguridad y Acceso
- **Login:** Acceso seguro con traducción de errores al español.
- **Registro:** Alta de nuevos usuarios con validación de contraseñas (mínimo 6 caracteres).
- **Feedback:** Avisos visuales verdes (`Snackbar`) para confirmar acciones exitosas.

### Dashboard de Eventos
- Resumen ejecutivo: Total de eventos y pendientes de pago.
- Seguimiento visual: Barra de progreso de pago por cada evento.

### Administración (CRUD)
- Gestión centralizada de perfiles de usuario.
- Sincronización inmediata: Los cambios se reflejan en todos los dispositivos conectados al instante gracias a Firestore.

## Cómo ejecutar

1. Clona el repositorio.
2. **Importante:** Asegúrate de incluir tu archivo `google-services.json` en la carpeta `/app`.
3. Abre en **Android Studio Ladybug** o superior.
4. Sincroniza Gradle y ejecuta en un dispositivo con API 24+.

## Requisitos

- Android Studio Ladybug (2024.2.1)
- JDK 21
- Proyecto configurado en Firebase Console con Auth y Firestore habilitados.
