# MPAZ Pañol - Sistema Movil de Inventario y Reservas

 Asignatura: Desarrollo de Aplicaciones Móviles (DSY1105)
 Institución: Duoc UC
 Socio Formador: Escuela Escritora Marcela Paz (San Bernardo)

## Equipo de Desarrollo
  Samuel Enrique Koch Jofre (Backend, Persistencia Room y Recursos Nativos)
  Luciano Rodriguez (Frontend, UI/UX Jetpack Compose y Navegación)

## Descripción del Proyecto
MPAZ Pañol es un Producto Minimo Viable movil diseñado para digitalizar y centralizar el proceso de reserva y prestamo de equipamiento tecnológico (proyectores, notebooks, kits de robótica) en la Escuela Escritora Marcela Paz. La aplicacion resuelve conflictos operativos de disponibilidad mediante un catalogo en tiempo real y un sistema de validacion estricto.

## Arquitectura y Tecnologías
  Lenguaje: Kotlin
  Framework UI: Jetpack Compose (Material Design 3)
  Arquitectura: MVVM (Model-View-ViewModel)
  Persistencia Local: SQLite mediante framework Room (Inyección de Mock Data inicial)
  Gestión de Estado: StateFlow / Coroutines

## Funcionalidades Principales Implementadas
1. Catálogo en Tiempo Real: Visualización de stock de equipos con persistencia local.
2. Validación Crítica de Reservas: Regla de negocio que bloquea y rechaza requerimientos con menos de 3 horas de anticipación.
3. Consola de Administrador TI: Panel para auditar, aceptar o rechazar solicitudes exigiendo justificación obligatoria.
4. Recursos Nativos Integrados:
   Motor Háptico (La vibracion): Retroalimentacion fisica al incumplir la regla de 3 horas.
   Notificaciones Locales Push: Alertas integradas para recordatorios de devolución al aprobar un ticket.

## Pasos para Ejecutar el Proyecto
1. Clonar este repositorio en la maquina local.
2. Abrir el proyecto utilizando Android Studio (versión compatible con KSP y Compose).
3. Sincronizar el proyecto con Gradle (`Sync Project with Gradle Files`).
4. Seleccionar un emulador o dispositivo físico con API 24 o superior.
5. Presionar `Run` (Shift + F10).
6. Nota: Al iniciar la aplicación por primera vez, la base de datos Room ejecutara un Callback para inyectar automaticamente el catalogo ficticio requerido por el caso.
