# ChaquiApp 🚧
**Sistema de reportes de incidentes viales comunitarios**

Aplicación Android desarrollada como práctica calificada — Mini Hackathon.  
Permite a los vecinos reportar problemas en pistas y veredas de su distrito de forma centralizada.

---

## Equipo
| Nombre | Rol |
|--------|-----|
| [Diego Huacca Ccaso] | Desarrollo Android |
| [Daniel Jacobo Colque] | Desarrollo Android |
| [Orlando Huacasi Ccopa] | Desarrollo Android |

---

## Brief asignado
**Brief #1 — ChaquiApp**  
Cliente: Municipalidad de provincia  
Problema: Los reportes de incidentes viales se hacen por WhatsApp y se pierden. Se necesita un sistema mínimo que centralice y clasifique los reportes.

---

## Funcionalidades implementadas

- Registrar un incidente con tipo (categoría), descripción, ubicación textual y urgencia
- Ver la lista de todos los incidentes ordenados por urgencia (Alta primero)
- Cambiar el estado de un incidente: Reportado → En atención → Resuelto
- Filtrar la lista por tipo de incidente
- Crear categorías personalizadas con nombre y emoji
- Eliminar categorías (solo si no tienen incidentes asociados)
- Los datos persisten aunque la app se cierre (Room Database)

---

## Tecnologías utilizadas

- **Kotlin** — lenguaje principal
- **Jetpack Compose** — UI declarativa
- **Material Design 3** — componentes y estilos
- **Room** — persistencia local de datos
- **MVVM** — arquitectura (ViewModel + StateFlow)
- **Navigation Compose** — navegación entre pantallas
- **KSP** — procesador de anotaciones para Room

---

## Estructura del proyecto

```
app/src/main/java/com/example/datossinmvvm/
│
├── data/
│   ├── Incidente.kt          → Entidades Room: Categoria, Incidente (+ enums Urgencia, EstadoIncidente)
│   ├── Converters.kt         → TypeConverters para Room (Urgencia y EstadoIncidente)
│   ├── IncidenteDao.kt       → DAOs: CategoriaDao e IncidenteDao con queries y Flow
│   └── IncidenteDatabase.kt  → Singleton RoomDatabase, pre-carga categorías por defecto
│
├── ui/
│   ├── IncidenteViewModel.kt → ViewModel: StateFlow de incidentes y categorías, lógica de negocio
│   ├── ListaScreen.kt        → Pantalla 1: lista con filtros, chips de urgencia/estado
│   ├── FormularioScreen.kt   → Pantalla 2: formulario para registrar nuevo incidente
│   ├── DetalleScreen.kt      → Pantalla 3: detalle + botón cambiar estado
│   └── CategoriasScreen.kt   → Pantalla 4: gestión de categorías personalizadas
│
├── navigation/
│   └── NavGraph.kt           → Rutas y NavController, ViewModel compartido
│
└── MainActivity.kt           → Punto de entrada, lanza NavGraph
```

---

## Flujo de navegación

```
ListaScreen
    │
    ├──[FAB +]──────────────→ FormularioScreen
    │                              │
    │                         [Guardar]
    │                              │
    │◄─────────────────────────────┘
    │
    ├──[Tap en incidente]───→ DetalleScreen
    │                              │
    │                    [Cambiar estado]
    │                    Reportado → En atención → Resuelto
    │◄─────────────────────────────┘
    │
    └──[Ícono 🏷]──────────→ CategoriasScreen
                                   │
                              [Crear / Eliminar categorías]
                                   │
          ◄────────────────────────┘
```

---

## Modelo de datos (Room)

### Entidad `categorias`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK) | Autoincremental |
| nombre | String | Nombre de la categoría |
| emoji | String | Ícono representativo |

### Entidad `incidentes`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK) | Autoincremental |
| categoriaId | Int (FK) | Referencia a categorias.id |
| descripcion | String | Descripción del problema |
| ubicacion | String | Calle o referencia textual |
| urgencia | Urgencia | Enum: BAJA / MEDIA / ALTA |
| estado | EstadoIncidente | Enum: REPORTADO / EN_ATENCION / RESUELTO |
| fechaReporte | Long | Timestamp de creación |

**Relación:** Una categoría tiene muchos incidentes (1:N).  
**Cascade:** Si se elimina una categoría, se eliminan sus incidentes.

---

## Decisiones técnicas tomadas

### 1. Categorías como entidad Room en lugar de enum fijo
Las categorías se almacenan en una tabla `categorias` de Room en vez de ser un enum hardcodeado. Esto permite que el usuario cree sus propias categorías desde la app. La relación con `Incidente` se maneja con `ForeignKey` y `CASCADE` para mantener integridad referencial. Al iniciar la BD por primera vez, un `RoomDatabase.Callback` inserta 4 categorías por defecto.

### 2. El cambio de estado vive en el ViewModel, no en el Composable
El botón "Cambiar estado" en `DetalleScreen` llama a `viewModel.avanzarEstado(incidente)`. El ViewModel contiene la lógica de transición (Reportado → En atención → Resuelto) y actualiza Room con una corrutina. El Composable nunca accede a Room directamente — solo observa el estado via `collectAsState()`. Esto permite testear la lógica de transición de forma aislada, sin depender de la UI.

### 3. StateFlow + flatMapLatest para filtros reactivos
El ViewModel expone un `MutableStateFlow<Int?>` para el filtro de categoría activo. La lista de incidentes se construye con `flatMapLatest`: cuando el filtro cambia, se cancela el Flow anterior y se lanza uno nuevo con la query correcta. La UI solo llama `viewModel.setFiltro(id)` y se recompone sola — cero lógica de filtrado en el Composable.

---

## Screenshots

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/c7c6ddac1055c08ef4594fd15212ffd728584588/WhatsApp%20Image%202026-05-04%20at%206.06.47%20PM%20(1).jpeg)

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/c7c6ddac1055c08ef4594fd15212ffd728584588/WhatsApp%20Image%202026-05-04%20at%206.06.47%20PM%20(2).jpeg)

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/c7c6ddac1055c08ef4594fd15212ffd728584588/WhatsApp%20Image%202026-05-04%20at%206.06.47%20PM.jpeg)

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/c7c6ddac1055c08ef4594fd15212ffd728584588/WhatsApp%20Image%202026-05-04%20at%206.06.48%20PM.jpeg)

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/db318c43ba0b33b3bb5aa37cc49ae729511c6ef0/Captura%20de%20pantalla%202026-05-04%20183457.png)

![image alt](https://github.com/diegohuacca-hub/ChaquiApp/blob/db318c43ba0b33b3bb5aa37cc49ae729511c6ef0/Captura%20de%20pantalla%202026-05-04%20183516.png)
## Pregunta de sustento

**"¿Por qué pusiste el cambio de estado en el ViewModel y no directo en el Composable?"**

El Composable solo debe renderizar estado y emitir eventos — no contiene lógica de negocio. Si el cambio de estado estuviera en el Composable, necesitaríamos acceder a Room directamente desde la UI, violando el patrón MVVM.

Al ponerlo en el ViewModel:
- La lógica de transición es **testeable de forma aislada** (sin UI)
- El Composable es "tonto": solo llama `viewModel.avanzarEstado(inc)`
- Cuando Room actualiza el registro, el **Flow notifica automáticamente** y la UI se recompone sola
- Cumple **separación de responsabilidades**: UI = presentación, ViewModel = lógica

---

## Cómo ejecutar el proyecto

1. Clonar el repositorio
2. Abrir en Android Studio (versión Hedgehog o superior)
3. Sync Gradle
4. Ejecutar en emulador o dispositivo físico con Android 8.0+ (API 26)
