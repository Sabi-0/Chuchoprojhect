Requisitos Previos

Android Studio (versión Arctic Fox o superior recomendada)

JDK 11 instalado y configurado

Dispositivo o emulador con Android 8.0 (API 26) o superior

Pasos para Configuración Inicial
Clonar el repositorio:
git clone https://github.com/tu-usuario/chuchoproject.git
cd chuchoproject

Sincronizar dependencias:
Abre el proyecto en Android Studio
Esperar a que se sincronicen las dependencias
"File > Sync Project with Gradle Files"
-----------------------------------------------------------------------------------------------------------------------
Estructura del proyecto
![image](https://github.com/user-attachments/assets/7004abe5-f0d2-4000-92fb-477073633188)

-----------------------------------------------------------------------------------------------------------------------
Flujo Básico de la Aplicación

Pantalla de Inicio:
Muestra lista de perros disponibles
Barra de búsqueda funcional

Agregar Perro:
Formulario con campos: nombre, descripción, ubicación, contacto e imagen
Los datos se guardan en Firestore/Firebase Storage

Autenticación:
Login con Email/Google
Modo invitado disponible

-----------------------------------------------------------------------------------------------------------------------
Documentacion Funciones principales:

-AuthViewModel.kt-
/**
 * Maneja la lógica de autenticación de la aplicación
 * @property userState Estado actual del usuario (observable)
 * @method signInWithEmail Autentica con email/contraseña
 * @param email String Correo del usuario
 * @param password String Contraseña
 * @param onComplete Callback (success: Boolean, errorMsg: String?)
 * 
 * Ejemplo:
 * authViewModel.signInWithEmail("user@example.com", "pass123") { success, msg -> 
 *     if (success) navController.navigate("home")
 * }
 */
class AuthViewModel : ViewModel() {
    // Implementación completa
}

-LoginScreen.kt-
/**
 * Pantalla de login/registro
 * @param authViewModel ViewModel de autenticación
 * @param navController Controlador de navegación
 * @param onDismiss Callback al cerrar el diálogo
 * 
 * Estados:
 * - isSignUp: Alterna entre login/registro
 * - errorMessage: Mensaje de error visualizado
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    // Implementación...
}

-PantallaListaPerros.kt-
/**
 * Muestra la lista de perros disponibles con capacidad de filtrado
 * @state listaPerros Lista completa de perros
 * @state filtro Texto de búsqueda actual
 * @state isLoading Estado de carga
 * 
 * Comportamiento:
 * - Al iniciar carga datos de Firestore
 * - Filtra en tiempo real según texto ingresado
 * - Muestra estados: loading, vacío, error
 */
@Composable
fun PantallaListaPerros() {
    // Implementación...
}

-AgregarPerroScreen.kt-
/**
 * Formulario para añadir nuevos perros
 * @param onSubmit Callback al enviar el formulario
 *   Parámetros: nombre, descripción, lugar, contacto, uri imagen
 * 
 * Flujo:
 * 1. Usuario completa formulario
 * 2. Selecciona imagen (opcional)
 * 3. Al enviar llama a onSubmit con los datos
 */
@Composable
fun AgregarPerroScreen(
    onSubmit: (String, String, String, String, Uri?) -> Unit
) {
    // Implementación...
}

3. Utilidades (util/) 

-FirestoreUtils.kt-
/**
 * Guarda un perro en Firestore
 * @param nombre String Nombre del perro
 * @param descripcion String Descripción
 * @param lugar String Ubicación
 * @param contacto String Teléfono
 * @param imageUrl String URL de la imagen
 * @param onSuccess Callback al éxito
 * @param onFailure Callback al fallar
 * 
 * Estructura en Firestore:
 * /perros/{documentID}
 *   - nombre: string
 *   - descripcion: string
 *   - lugar: string
 *   - contacto: string
 *   - imagenUrl: string
 *   - timestamp: number
 */
fun guardarPerroEnFirestore(
    nombre: String,
    descripcion: String,
    lugar: String,
    contacto: String,
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    // Implementación...
}

-StorageUtils.kt-
/**
 * Sube una imagen a Firebase Storage
 * @param uri Uri? URI de la imagen local
 * @param onSuccess Callback con URL de descarga
 * @param onFailure Callback al fallar
 * 
 * Estructura en Storage:
 * /imagenesPerros/{UUID}.jpg
 */
fun subirImagenAFirebase(
    uri: Uri?,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    // Implementación...
}

4 Navegacion
-MainActivity.kt-
/**
 * Configuración principal de navegación
 * Rutas:
 * - screen1: PantallaListaPerros
 * - screen2: AgregarPerroScreen
 * - screen3: ProfileScreen (requiere auth)
 * 
 * Estructura:
 * - BottomNavigationBar para cambiar entre pantallas
 * - TopAppBar con botón de auth
 */
@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    // Implementación...
}

Modelo de Datos
-Perro.kt-
/**
 * Modelo de datos para perros
 * @property nombre String Nombre del perro
 * @property descripcion String Descripción
 * @property lugar String Ubicación
 * @property imagenUrl String URL de imagen en Storage
 * @property contacto String Teléfono de contacto
 * 
 * Uso:
 * - Se mapea automáticamente desde Firestore
 * - Todos los campos tienen valores por defecto
 */
data class Perro(
    val nombre: String = "",
    val descripcion: String = "",
    val lugar: String = "",
    val imagenUrl: String = "",
    val contacto: String = ""
)
------------------------------------------------------------------------------------------------------------
Diagrama de flujo:

![image](https://github.com/user-attachments/assets/1bac8c7b-bbe6-4677-922a-3f92720bd2b5)


