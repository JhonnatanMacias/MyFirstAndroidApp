# Buscador de libros

Aplicación Android para buscar libros en [Open Library](https://openlibrary.org/). Escribe un título o tema para consultar resultados y toca un libro para ver su ficha.

## Funciones

- Busca libros por título o tema.
- Muestra el título y el autor de cada resultado.
- Abre una ficha con portada, autor, año de primera publicación y descripción cuando esos datos están disponibles.
- Consulta los datos de Open Library y carga las portadas desde su servicio de imágenes.

## Capturas

### Búsqueda

![Pantalla de búsqueda de libros](docs/screenshots/busqueda-libros.png)

### Detalle del libro

![Ficha de un libro con portada y descripción](docs/screenshots/detalle-libro.png)

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- ViewModel y StateFlow para mantener y exponer el estado de la pantalla
- Coroutines para ejecutar las solicitudes de red fuera del hilo principal
- Coil 3 para cargar las portadas
- Open Library Search API para buscar libros y API de obras para consultar descripciones

## Arquitectura

La interfaz está separada del acceso a datos. `MainActivity` crea el ViewModel y conecta la ruta Compose; las pantallas reciben el estado y notifican acciones mediante callbacks.

```text
MainActivity
    └── BooksRoute
        ├── BooksScreen / BookDetailScreen
        └── BooksViewModel ── BookRepository
                                  └── OpenLibraryBookRepository
```

- `model/Book.kt`: modelo de libro usado por la aplicación.
- `data/BookRepository.kt`: contrato para buscar libros y obtener descripciones.
- `data/OpenLibraryBookRepository.kt`: implementación que consulta Open Library y convierte las respuestas JSON en modelos. Usa coroutines en `Dispatchers.IO`.
- `ui/books/BooksViewModel.kt`: coordina búsquedas, selección y carga de descripción; publica `BooksUiState` mediante `StateFlow`.
- `ui/books/BooksScreen.kt` y `BookDetailScreen.kt`: componentes Compose que muestran el estado y envían acciones.
- `ui/books/BooksRoute.kt`: conecta el ViewModel con las pantallas.

El flujo de datos va desde una acción de la UI al ViewModel, luego al repositorio y vuelve como un nuevo estado que Compose observa. El repositorio se entrega al ViewModel mediante una factory sencilla, así que la app mantiene la arquitectura desacoplada sin añadir un framework de inyección de dependencias.

## Ejecutar el proyecto

Abre el proyecto en Android Studio, sincroniza Gradle y ejecútalo en un emulador o dispositivo Android. Se requiere conexión a Internet para buscar libros, portadas y descripciones.
