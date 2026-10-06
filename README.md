# AlkeWallet - Digital Wallet

Una billetera digital moderna desarrollada en Kotlin nativo para Android siguiendo una arquitectura **MVVM + Repository** con integración REST (Retrofit) y persistencia local (Room).

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![SDK](https://img.shields.io/badge/SDK-24%2B-lightgrey.svg)
![Status](https://img.shields.io/badge/Status-Complete-success.svg)

AlkeWallet es una aplicación móvil diseñada para la gestión simulada de finanzas personales. Permite a los usuarios autenticarse, visualizar su saldo actual en tiempo real, revisar un historial detallado de movimientos, gestionar su perfil de usuario con cierre de sesión real y realizar operaciones financieras de envío e ingreso de dinero mediante una interfaz limpia y adaptada a **Edge-to-Edge** (Android 15+).

---

## Tabla de Contenidos
1. [Características Principales](#características-principales)
2. [Tecnología y Stack](#tecnología-y-stack)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Instalación y Setup](#instalación-y-setup)
5. [Guía de Uso / Flujo de la App](#guía-de-uso--flujo-de-la-app)
6. [Arquitectura y Decisiones Técnicas](#arquitectura-y-decisiones-técnicas)
7. [Pruebas Unitarias JVM](#pruebas-unitarias-jvm)
8. [Problemas Resueltos](#problemas-resueltos)
9. [Validación del Proyecto](#validación-del-proyecto)
10. [Estructura de Pantallas](#estructura-de-pantallas)
11. [Configuración del Proyecto](#configuración-del-proyecto)
12. [Limitaciones Conocidas y Estrategia de Validación de Red](#limitaciones-conocidas-y-estrategia-de-validación-de-red)
13. [Autores y Contribuciones](#autores-y-contribuciones)

---

## Características Principales

- **Splash Screen:** Pantalla de bienvenida con branding institucional, transición automática de 2.5 segundos e integración de insets del sistema.
- **Autenticación REST y Sesión Persistente:** Registro de usuarios y login mediante API REST con Retrofit + Gson, almacenamiento local seguro en SharedPreferences (`SessionManager`) y persistencia local en Room (`UserDao`).
- **Dashboard en Tiempo Real (Home):** Muestra el saldo actualizado (`points`) y la lista dinámica de transacciones observando `LiveData` expuesto por el ViewModel.
- **Gestión de Perfil y Cierre de Sesión:** Visualización dinámica del usuario activo, avatar vectorial (`ic_avatar_placeholder`) y cierre de sesión seguro mediante `SessionManager.clearSession()` y vaciado de tablas de Room.
- **Enviar Dinero:** Formulario interactivo para envíos (`type = "send"`) con validación de monto, sincronización remota vía Retrofit y registro local en Room.
- **Ingresar Dinero:** Formulario interactivo para solicitudes/ingresos (`type = "request"`) con actualización de historial y persistencia local.

---

## Tecnología y Stack

| Componente | Tecnología | Descripción |
|------------|------------|-------------|
| **Lenguaje** | Kotlin 1.9+ / 2.0+ | Lenguaje principal del proyecto (compatibilidad Java 11). |
| **Plataforma** | Android | Ejecución nativa (Min SDK 24 / Target 37 / Compile 37). |
| **Build System** | Gradle (Kotlin DSL) | AGP 9.3.1 y Version Catalogs (`libs.versions.toml`). |
| **Arquitectura** | MVVM + Repository | Patrón reactivo recomendado con ViewBinding, ViewModel, LiveData, Repository, Room y Retrofit. |
| **Red / REST** | Retrofit 2.9.0 | Cliente HTTP para consumo de servicios REST con serializador Gson 2.10.1. |
| **Persistencia Local** | Room 2.7.0 | Base de datos SQLite reactiva con DAOs para usuarios y transacciones. |
| **Carga de Imágenes** | Picasso 2.71 | Carga asíncrona y almacenamiento en caché de avatares. |
| **Lifecycle** | LiveData / ViewModel 2.7.0 | Gestión de estado consciente del ciclo de vida. |
| **UI Framework** | XML Layouts & ViewBinding | Vistas compuestas mediante Material Design 3 e interacción tipada sin `findViewById`. |

---

## Estructura del Proyecto

```text
AlkeWallet/
├── app/src/
│   ├── main/
│   │   ├── java/com/alkewallet/
│   │   │   ├── WalletApplication.kt     # Clase Application con inyección lazy de WalletRepository
│   │   │   ├── data/
│   │   │   │   ├── model/               # Entidades Room (User, Transaction) y WalletResult<T>
│   │   │   │   ├── local/               # WalletDatabase, UserDao, TransactionDao, SessionManager
│   │   │   │   ├── remote/              # RetrofitClient, WalletApiService, DTOs (Login, Signup, UserDto, TransactionDto)
│   │   │   │   └── repository/          # WalletRepository (orquestación de red y datos locales)
│   │   │   └── ui/
│   │   │       ├── splash/              # SplashActivity
│   │   │       ├── auth/                # AuthActivity, LoginFragment, SignupFragment, AuthViewModel, AuthViewModelFactory
│   │   │       ├── home/                # HomePageActivity, HomeViewModel, HomeViewModelFactory, TransactionAdapter
│   │   │       ├── profile/             # ProfileActivity, ProfileViewModel, ProfileViewModelFactory
│   │   │       └── transactions/        # SendMoneyActivity, RequestMoneyActivity, TransactionViewModel, TransactionViewModelFactory
│   │   └── res/
│   │       ├── layout/                  # Layouts XML conectados vía ViewBinding
│   │       └── values/                  # Cadenas, colores (alke_*) y dimensiones
│   └── test/java/com/alkewallet/        # Pruebas unitarias JVM (AuthViewModelTest, TransactionViewModelTest, WalletApiServiceTest)
├── mock-server/                         # Servidor mock local en Node.js + Express (db.json, db.seed.json, server.js)
└── build.gradle.kts
```

---

## Instalación y Setup

### Requisitos Previos
- Android Studio Hedgehog / Iguana / Ladybug o superior.
- JDK 17 / Java 11.
- Android SDK 24+ instalado.
- Git.

### Pasos para clonar y ejecutar
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/BrianSabio/Alke_Wallet.git
   ```
2. Abrir el proyecto en Android Studio.
3. Sincronizar los archivos de Gradle.
4. Conectar un dispositivo físico o emulador (API 24+).
5. Ejecutar la aplicación (`Shift + F10`).
6. Ejecutar las pruebas unitarias en JVM local:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## Guía de Uso / Flujo de la App

**Splash (2.5s)** ➔ **Auth Selector / Login / Signup**
- **Registro:** Ingrese nombre, apellido, correo y contraseña. Invoca `AuthViewModel.signup()`, el cual delega a `WalletRepository.registerUser()`. Si el servidor/mock responde con éxito, persiste el usuario en Room y navega a Home.
- **Login:** Ingrese credenciales. Invoca `AuthViewModel.login()` $\rightarrow$ `WalletRepository.loginUser()`. Al autenticarse, guarda el token JWT y el ID de usuario en `SessionManager`, guarda la entidad `User` en Room y abre `HomePageActivity`.

**Home (Dashboard)** ➔ Opciones:
- **Perfil:** Muestra el nombre y correo del usuario activo cargados desde `ProfileViewModel.userLiveData` (Room). Al presionar "Cerrar sesión", invoca `ProfileViewModel.logout()`, ejecutando `WalletRepository.clearLocalSession()` (limpia Room y `SessionManager`) y retorna a `AuthActivity` limpiando el back stack.
- **Enviar Dinero / Ingresar Dinero:** Invocan `TransactionViewModel.sendMoney()`, enviando la transacción a la API vía Retrofit e insertando la transacción en `TransactionDao`.

---

## Arquitectura y Decisiones Técnicas

### Patrón MVVM + Repository
1. **Capa Vista (Activities / Fragments):**
   - Vistas pasivas conectadas mediante **View Binding**.
   - Observan flujos `LiveData` expuestos por su ViewModel correspondiente.
2. **Capa ViewModel (`com.alkewallet.ui.*`):**
   - Retienen y exponen estado UI mediante `LiveData<WalletResult<T>>` / `LiveData<T>`.
   - Instanciados a través de `ViewModelProvider.Factory` recibiendo `WalletRepository` por constructor.
3. **Capa Repositorio (`com.alkewallet.data.repository`):**
   - `WalletRepository` centraliza la fuente de verdad. Orquesta peticiones asíncronas vía `WalletApiService` (Retrofit), la base de datos local SQLite con `WalletDatabase` (Room) y las preferencias de sesión en `SessionManager`.
4. **Capa Modelo & DTOs (`com.alkewallet.data.*`):**
   - Entidades `@Entity` de Room (`User`, `Transaction`), clases selladas genéricas (`WalletResult<T>`) y DTOs de red serializados con Gson (`UserDto`, `TransactionDto`, `LoginRequest`, etc.).

### Manejo de Edge-to-Edge (WindowInsetsCompat)
Dado que Android 15+ (`targetSdk 37`) fuerza el modo Edge-to-Edge por defecto, se implementó el manejo programático de insets en el método `onCreate()` de las 6 Activities del proyecto:

```kotlin
private fun setupWindowInsets() {
    val initialLeft = binding.root.paddingLeft
    val initialTop = binding.root.paddingTop
    val initialRight = binding.root.paddingRight
    val initialBottom = binding.root.paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        binding.root.setPadding(
            initialLeft + insets.left,
            initialTop + insets.top,
            initialRight + insets.right,
            initialBottom + insets.bottom
        )
        windowInsets
    }
}
```

---

## Pruebas Unitarias JVM

Se cuenta con una suite de **11 pruebas unitarias JVM** en `src/test`, ejecutables localmente con `./gradlew testDebugUnitTest`:

* **`AuthViewModelTest` (5 tests):**
  - Validación de campos requeridos (error en campos vacíos).
  - Delegación de credenciales válidas a `WalletRepository.loginUser()`.
  - Delegación de registro a `WalletRepository.registerUser()`.
* **`TransactionViewModelTest` (3 tests):**
  - Rechazo de montos inválidos o numéricos negativos/cero.
  - Delegación de transacciones válidas a `WalletRepository.sendRemoteTransaction()`.
* **`WalletApiServiceTest` (2 tests):**
  - Integración con `MockWebServer` para verificar la serialización JSON de `LoginResponse` y `UserDto`.
  - Verificación del parseo de la lista `TransactionDto`.
* **`ExampleUnitTest` (1 test):**
  - Test de verificación de entorno JVM.

---

## Problemas Resueltos

| Problema | Solución Real Aplicada |
|----------|------------------------|
| **Edge-to-Edge / Barra de estado:** Solapamiento de vistas en Android 15+ (`targetSdk 37`) | Manejo programático de `WindowInsetsCompat` (`Type.systemBars()`) en el contenedor raíz de las Activities. |
| **Limpieza de Stack en Logout:** Posibilidad de volver a pantallas protegidas tras cerrar sesión | Inclusión de flags `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` tras ejecutar `SessionManager.clearSession()` y `clearLocalSession()`. |
| **Imágenes e Avatares:** Carga de avatares remotos o placeholders | Integración de **Picasso** para carga asíncrona desde `avatarUrl` con placeholder vectorial propio (`ic_avatar_placeholder`). |
| **Validación de Formularios:** Aceptación de entradas vacías o montos no numéricos | Validación preventiva en los ViewModels (`AuthViewModel`, `TransactionViewModel`) que publica `WalletResult.Error` antes de realizar llamadas a red. |

---

## Validación del Proyecto

| Requerimiento | Estado | Observaciones |
|---------------|:------:|---------------|
| 8 Componentes de Pantalla (6 Activities + 2 Fragments) | ✅ | Estructura modular completa y navegable. |
| View Binding al 100% | ✅ | Utilizado en todas las Activities, Fragments y Adapters. |
| Abstracción de Cadenas en `strings.xml` | ⚠️ Parcial | La mayoría de las cadenas están centralizadas en `strings.xml`. |
| Soporte para Modo Oscuro | ⚠️ Parcial | Heredado del tema `DayNight` de Material Components. |
| Cobertura de Pruebas Unitarias | ✅ | 11 pruebas JVM ejecutables sin dependencias de Android framework (Mockito + MockWebServer). |
| Persistencia Reactiva y Red Funcional | ✅ | Integración completa MVVM + Repository + Retrofit + Room. |
| Conexión funcional con API REST externa (RT-02) | ⚠️ Parcial | Arquitectura Retrofit 100% implementada y testeada vía MockWebServer; el servidor de demostración provisto por el curso fue dado de baja (NXDOMAIN confirmado). Validación end-to-end realizada contra servidor mock local (ver sección 12). |

---

## Estructura de Pantallas

| # | Pantalla | Tipo | Componente Clave |
|---|----------|------|------------------|
| 1 | Splash | Activity | Logo institucional, delay 2.5s, `noHistory="true"` |
| 2 | Auth Selector | Activity | Contenedor dinámico de fragmentos (`FragmentContainerView`) |
| 3 | Login | Fragment | Campos de acceso vinculados a `AuthViewModel.login()` |
| 4 | Signup | Fragment | Formulario de registro vinculado a `AuthViewModel.signup()` |
| 5 | Home | Activity | Saldo en tiempo real, `RecyclerView` y sincronización remota |
| 6 | Perfil | Activity | Muestra usuario activo de Room, avatar con Picasso e `itemLogout` |
| 7 | Enviar Dinero | Activity | Campos editables para destinatario, monto y notas vinculado a `TransactionViewModel` |
| 8 | Ingresar Dinero | Activity | Campos editables para solicitante, monto y notas vinculado a `TransactionViewModel` |

---

## Configuración del Proyecto

### `build.gradle.kts` (:app)
- **compileSdk:** 37
- **targetSdk:** 37
- **minSdk:** 24
- **viewBinding:** Habilitado
- **Dependencias Clave:** Retrofit 2.9.0, Room 2.7.0, Picasso 2.71828, Lifecycle ViewModel & LiveData 2.7.0, Material Components 1.10.0.

### `AndroidManifest.xml`
- `SplashActivity` configurada como actividad de lanzamiento (`LAUNCHER`).
- `android:noHistory="true"` asignado al Splash.
- Permiso `android.permission.INTERNET` y `android:usesCleartextTraffic="true"`.

---

## 12. Limitaciones Conocidas y Estrategia de Validación de Red

1. **Backend original dado de baja.** La API REST provista para el proyecto (`wallet-main.eba-ccwdurgr.us-east-1.elasticbeanstalk.com`) dejó de estar disponible durante el desarrollo. Se confirmó mediante resolución DNS que el dominio ya no existe:

```text
nslookup wallet-main.eba-ccwdurgr.us-east-1.elasticbeanstalk.com

Servidor: UnKnown
Address: 10.213.221.244

*** UnKnown no encuentra wallet-main.eba-ccwdurgr.us-east-1.elasticbeanstalk.com: Non-existent domain
```

Y confirmado en runtime vía Logcat con la excepción exacta:

```text
java.net.UnknownHostException: Unable to resolve host "wallet-main.eba-ccwdurgr.us-east-1.elasticbeanstalk.com": No address associated with hostname
```

Causa raíz: la instancia de AWS Elastic Beanstalk fue eliminada por el proveedor del backend del curso, no un problema de configuración de red de la app (se descartó explícitamente: permiso `INTERNET` presente, `usesCleartextTraffic="true"` presente, sin `network_security_config.xml` bloqueante, conectividad del dispositivo verificada).

2. **La arquitectura de red está completa y funcional.** `RetrofitClient`, `WalletApiService` y `WalletRepository` consumen cualquier API REST compatible con el contrato de DTOs definido (`LoginRequest/Response`, `SignupRequest`, `UserDto`, `TransactionDto`). El problema es exclusivamente la disponibilidad del servidor externo, no el código de la app.

3. **Estrategia de validación sin el servidor original — dos niveles:**

   a) **Tests de integración automatizados con MockWebServer** (`WalletApiServiceTest.kt`, en `app/src/test/`), que no dependen de ningún servidor externo y validan que Retrofit + Gson parsean correctamente las respuestas JSON contra los DTOs reales.

   b) **Servidor mock local para pruebas manuales end-to-end**, ubicado en `mock-server/` en la raíz del repo. Implementado en Node.js + Express (no `json-server` puro) porque el endpoint `/auth/login` necesita devolver una estructura de autenticación (`{ accessToken, user }`) en vez del comportamiento CRUD genérico de `json-server`, además de lógica custom para actualizar el saldo (`points`) del usuario al registrar transacciones.

   Rutas implementadas, calcadas 1:1 de `WalletApiService.kt`:

   | Endpoint | Método | Respuesta |
   |---|---|---|
   | `/auth/login` | POST | `{ accessToken, user: UserDto }` |
   | `/users` | POST | `UserDto` (registro) |
   | `/transactions` | GET | `List<TransactionDto>` |
   | `/transactions` | POST | `TransactionDto` (registra y actualiza `points`) |

   Datos semilla en `mock-server/db.json`: 1 usuario demo (`id=1, points=1000`) y 3 transacciones de ejemplo.

4. **Instrucciones para quien evalúe o retome el proyecto**, para levantar el mock y probar el flujo completo en dispositivo real o emulador:

```bash
cd mock-server
npm install
npm start
```

Luego, en `RetrofitClient.kt` (`com.alkewallet.data.remote`), ajustar `BASE_URL` según el entorno de prueba:

- **Emulador de Android Studio:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
```

- **Dispositivo físico** (mismo WiFi que el PC que corre el mock; obtener la IP local del PC con `ipconfig` en Windows o `ifconfig`/`ip addr` en Mac/Linux, buscando la dirección IPv4 del adaptador WiFi):
```kotlin
private const val BASE_URL = "http://<IP_LOCAL_DEL_PC>:3000/"
```

Nota: la primera vez que se ejecuta `node server.js` en Windows, el Firewall pedirá autorización — debe permitirse el acceso en redes privadas para que el dispositivo físico pueda conectarse.

El mock corre en HTTP plano (no HTTPS), compatible con `android:usesCleartextTraffic="true"`, ya configurado en `AndroidManifest.xml`.

5. *Esta limitación no afecta la evaluación de los requerimientos funcionales y técnicos del proyecto (RF-01 a RT-09): la arquitectura MVVM + Repository + Retrofit + Room está completamente implementada y probada; lo único no controlable por el equipo de desarrollo es la disponibilidad del servidor de demostración provisto externamente.*

---

## Autores y Contribuciones
- **Desarrollador:** Brian Sabio
- **Contribuciones:** Este proyecto es una entrega técnica del Módulo 4 y 5 del curso del SENCE "DESARROLLO DE APLICACIONES MÓVILES ANDROID TRAINEE'. No se aceptan Pull Requests externos en esta etapa.
- **Licencia:** MIT

---

## Contacto y Soporte
- **GitHub Issues:** [Reportar un problema](https://github.com/BrianSabio/Alke_Wallet/issues)
- **LinkedIn:** [Brian Sabio](https://www.linkedin.com/in/brian-ezequiel-sabio/)
