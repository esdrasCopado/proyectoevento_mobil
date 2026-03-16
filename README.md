# ProyectoEvento

Aplicación móvil Android para la gestión y seguimiento de eventos sociales y corporativos.

## Descripción

ProyectoEvento permite registrar eventos como bodas, XV años, corporativos y más, haciendo seguimiento del estado de pago de cada uno. Desde la pantalla de inicio se visualiza un resumen general y la lista de eventos registrados, y desde la pantalla de nuevo evento se pueden agregar nuevos registros con validación de datos.

## Tecnologías

- **Lenguaje:** Kotlin 2.2.10
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Estado reactivo:** StateFlow + `collectAsStateWithLifecycle`
- **Build:** Gradle 9.0.0 con Kotlin DSL
- **SDK mínimo:** API 24 (Android 7.0)
- **SDK objetivo:** API 36

## Estructura del proyecto

```
app/src/main/java/com/itson/proyectoevento/
├── MainActivity.kt                  # Actividad principal y navegación
├── data/
│   └── model/
│       └── Evento.kt                # Modelo de datos del evento
└── ui/
    ├── inicio/
    │   ├── InicioScreen.kt          # Pantalla principal con lista de eventos
    │   └── InicioViewModel.kt       # Lógica y estado de la pantalla de inicio
    ├── newEvent/
    │   ├── NuevoEventoScreen.kt     # Formulario para crear un nuevo evento
    │   └── NuevoEventoViewModel.kt  # Lógica y validación del formulario
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Funcionalidades

### Pantalla de Inicio
- Resumen con total de eventos y eventos pendientes de pago
- Lista de eventos registrados con nombre, tipo, fecha y barra de progreso de pago
- Botón para navegar a la pantalla de creación de evento

### Pantalla Nuevo Evento
- Formulario con los siguientes campos:
  - **Nombre del evento** — texto libre
  - **Tipo de evento** — selector desplegable (Boda, XV Años, Corporativo, Cumpleaños, Graduación, Otro)
  - **Fecha** — formato DD/MM/AAAA
  - **Costo total** — valor numérico decimal
- Validación de todos los campos antes de guardar
- El evento se registra con 0% de pago inicial

## Modelo de datos

```kotlin
data class Evento(
    val id: Int,
    val nombre: String,
    val fecha: String,         // formato DD/MM/AAAA
    val porcentajePagado: Int, // 0 - 100
    val totalCosto: Double,
    val tipo: String
)
```

## Cómo ejecutar

1. Clona el repositorio
2. Abre el proyecto en **Android Studio Hedgehog** o superior
3. Sincroniza las dependencias con Gradle
4. Ejecuta en un emulador o dispositivo físico con Android 7.0+

```bash
git clone <url-del-repositorio>
```

## Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17+
- Android SDK con API 24 o superior instalada
