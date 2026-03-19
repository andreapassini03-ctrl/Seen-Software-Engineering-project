package com.example.progetto_software

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.progetto_software.screen.*
import com.example.progetto_software.theme.Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.controller.LoginState
import com.example.progetto_software.controller.LoginViewModel
import com.example.progetto_software.controller.LoginViewModelFactory
import com.example.progetto_software.database.MyApplicationClass
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory

import com.example.progetto_software.database.DatabasePopulator
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.progetto_software.controller.SchedaContenutoCatalogoViewModel
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel
import com.example.progetto_software.controller.SezioneViewModel
import com.example.progetto_software.data.Contenuto
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import kotlinx.coroutines.launch
import java.net.URLEncoder

class MainActivity : ComponentActivity() {

    private val TAG = "DatabasePopulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MyApplication onCreate avviato.")

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val isDbPopulated = sharedPrefs.getBoolean("is_db_populated", false)

                Log.d(TAG, "isDbPopulated: $isDbPopulated")

                //DatabasePopulator.startClearing(applicationContext)

                //DatabasePopulator.populateDatabase(applicationContext)

                if (!isDbPopulated) {
                    Log.d(TAG, "Il database non è ancora popolato. Avvio il popolamento...")

                    try {
                        DatabasePopulator.populateDatabase(applicationContext)
                        sharedPrefs.edit().putBoolean("is_db_populated", true).apply()
                        Log.d(TAG, "Popolamento database completato con successo.")
                    } catch (e: Exception) {
                        Log.e(TAG, "Errore durante il popolamento del database: ${e.message}", e)
                    }
                } else {
                    Log.d(TAG, "Database già popolato. Nessun popolamento richiesto.")
                }
            } else {
                Log.w(
                    TAG,
                    "Versione Android inferiore a Oreo (API 26). Il popolamento del database non verrà eseguito."
                )
            }
        }
        setContent {
            Theme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val application = context.applicationContext as MyApplicationClass

                // DAO instances (obtained once and passed to ViewModels)
                val utenteDao = application.database.utenteDao()
                val filmDao = application.database.filmDao()
                val serieTvDao = application.database.serieTvDao()
                val recensioneDao = application.database.recensioneDao()
                val contenutoUtenteDao = application.database.contenutoUtenteDao()
                val episodioDao = application.database.episodioDao() // NUOVO: Ottieni l'istanza di EpisodioDao


                // ViewModel instances (created once and passed where needed)
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(utenteDao)
                )
                val utenteViewModel: UtenteViewModel = viewModel(
                    factory = UtenteViewModelFactory(utenteDao, application)
                )
                val sezioneViewModel: SezioneViewModel = viewModel(
                    factory = SezioneViewModel.SezioneViewModelFactory(
                        contenutoUtenteDao,
                        utenteDao,
                        filmDao,
                        serieTvDao,
                        application
                    )
                )

                val schedaContenutoCatalogoViewModel: SchedaContenutoCatalogoViewModel = viewModel(
                    factory = SchedaContenutoCatalogoViewModel.SchedaContenutoCatalogoViewModelFactory(
                        filmDao,
                        serieTvDao,
                        recensioneDao,
                        contenutoUtenteDao
                    )
                )
                // NEW: ViewModel for the Personal Content Detail Screen
                val schedaContenutoPersonaleViewModel: SchedaContenutoPersonaleViewModel = viewModel(
                    factory = SchedaContenutoPersonaleViewModel.SchedaContenutoPersonaleViewModelFactory(
                        filmDao,
                        serieTvDao,
                        recensioneDao,
                        contenutoUtenteDao,
                        episodioDao,
                        database = application.database // NUOVO: Passa episodioDao alla factory
                    )
                )

                NavHost(
                    navController = navController,
                    startDestination = AppScreen.Login.route
                ) {
                    composable(AppScreen.Login.route) {
                        MainScreen(
                            onLoginSuccess = {
                                navController.navigate(AppScreen.Utente.route) {
                                    popUpTo(AppScreen.Login.route) { inclusive = true }
                                }
                            },
                            onRegisterClick = {
                                navController.navigate(AppScreen.Registrazione.route)
                            },
                            utenteViewModel = utenteViewModel,
                            loginViewModel = loginViewModel
                        )
                    }

                    composable(AppScreen.Utente.route) {
                        val currentUtenteViewModel: UtenteViewModel = viewModel(
                            factory = UtenteViewModelFactory(application.database.utenteDao(), application)
                        )
                        UtenteScreen(
                            onCatalogoClick = { navController.navigate(AppScreen.Catalogo.route) },
                            onAreaPersonaleClick = { /* Already in UtenteScreen */ },
                            onSezioneTvClick = { mediaType ->
                                navController.navigate(AppScreen.Sezione.createRoute(mediaType))
                            },
                            utenteViewModel = currentUtenteViewModel // Use the local ViewModel for this screen
                        )
                    }

                    // Definizione della rotta per la schermata Sezione (Film/Serie TV per l'utente)
                    composable(
                        route = AppScreen.Sezione.route,
                        arguments = listOf(
                            navArgument("mediaType") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val mediaType = backStackEntry.arguments?.getString("mediaType") ?: ""

                        SezioneScreen(
                            title = mediaType,
                            onBackClick = { navController.popBackStack() },
                            onCatalogoClick = {
                                navController.navigate(AppScreen.Catalogo.route) {
                                    popUpTo(AppScreen.Catalogo.route) { inclusive = true }
                                }
                            },
                            onAreaPersonaleClick = { /* Already in UtenteScreen */ },
                            onContentCardClick = { contenuto ->
                                val contentId = contenuto.id
                                val contentType = when (contenuto) {
                                    is Film -> "film"
                                    is SerieTv -> "serieTv"
                                    else -> throw IllegalArgumentException("Unknown content type for navigation")
                                }
                                // Navigate to the personal detail screen from a personal section
                                navController.navigate(AppScreen.SchedaContenutoPersonale.createRoute(contentId, contentType))
                            }
                        )
                    }

                    // NEW: Definizione della rotta per la SchedaContenutoPersonaleScreen
                    composable(
                        route = AppScreen.SchedaContenutoPersonale.route,
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.StringType },
                            navArgument("contentType") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val contentId = backStackEntry.arguments?.getString("contentId")
                        val contentType = backStackEntry.arguments?.getString("contentType")

                        val currentLoggedInUser by utenteViewModel.loggedInUser.collectAsState()

                        Log.d("SchedaContenutoPersonaleNav", "Navigated to SchedaContenutoPersonaleScreen. contentId: $contentId, contentType: $contentType, loggedInUser: ${currentLoggedInUser?.id}")


                        if (contentId != null && contentType != null) {
                            SchedaContenutoPersonaleScreen(
                                contentId = contentId,
                                contentType = contentType,
                                onFlagReviewClick = { }, // Implement if needed
                                onBackClick = { navController.popBackStack() },
                                onCatalogoClick = {
                                    navController.navigate(AppScreen.Catalogo.route) {
                                        popUpTo(AppScreen.Catalogo.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onAreaPersonaleClick = { /* Already in UtenteScreen */ },
                                onNavigateToSegnalazione = {
                                    navController.navigate(AppScreen.Segnalazione.route)
                                },
                                viewModel = schedaContenutoPersonaleViewModel,
                                // Pass the contentId, contentType, and loggedInUserId to the RecensioneScreen
                                onRecensioneClick = { contentIdToReview, contentTypeToReview, loggedInUserIdToReview ->
                                    navController.navigate(
                                        AppScreen.Recensione.createRoute(
                                            contentIdToReview,
                                            contentTypeToReview,
                                            loggedInUserIdToReview
                                        )
                                    )
                                }
                            )
                        } else {
                            Log.e("NavigationError", "contentId or contentType was null for SchedaContenutoPersonaleScreen")
                            navController.popBackStack()
                        }
                    }

                    composable(AppScreen.Catalogo.route) {
                        CatalogoScreen(
                            onBackClick = { navController.popBackStack() },
                            onCatalogoClick = { /* Already in Catalogo */ },
                            onAreaPersonaleClick = { navController.navigate(AppScreen.Utente.route) },
                            onContentClick = { contentId, contentType -> // Changed to pass ID and Type
                                navController.navigate(
                                    AppScreen.SchedaContenutoCatalogo.createRoute(
                                        contentId,
                                        contentType
                                    )
                                )
                            }
                        )
                    }

                    composable(AppScreen.Registrazione.route) {
                        RegistrazioneScreen(
                            onRegistrationSuccess = {
                                navController.navigate(AppScreen.Login.route) {
                                    popUpTo(AppScreen.Registrazione.route) { inclusive = true }
                                }
                            },
                            onBackClick = { navController.popBackStack() },
                            utenteViewModel = utenteViewModel
                        )
                    }

                    composable(AppScreen.Segnalazione.route) {
                        SegnalazioneScreen(
                            onBackClick = { navController.popBackStack() },
                            onReportSubmitted = {
                                navController.popBackStack()
                                Toast.makeText(context, "Segnalazione inviata correttamente", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // *** FIX STARTS HERE ***
                    composable(
                        route = AppScreen.Recensione.route, // Use the route with arguments
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.StringType },
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("loggedInUserId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val contentId = backStackEntry.arguments?.getString("contentId")
                        val contentType = backStackEntry.arguments?.getString("contentType")
                        val loggedInUserId = backStackEntry.arguments?.getString("loggedInUserId")

                        // Log for debugging
                        Log.d("RecensioneScreenNav", "Navigated to RecensioneScreen. contentId: $contentId, contentType: $contentType, loggedInUserId: $loggedInUserId")

                        if (contentId != null && contentType != null && loggedInUserId != null) {
                            RecensioneScreen(
                                onBackClick = { navController.popBackStack() },
                                onCatalogoClick = {
                                    navController.navigate(AppScreen.Catalogo.route) {
                                        popUpTo(AppScreen.Catalogo.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onAreaPersonaleClick = { navController.navigate(AppScreen.Utente.route) },
                                schedaContenutoPersonaleViewModel = schedaContenutoPersonaleViewModel, // Pass the instantiated ViewModel
                                contentId = contentId,
                                contentType = contentType,
                                loggedInUserId = loggedInUserId,
                            )
                        } else {
                            Log.e("NavigationError", "Missing arguments for RecensioneScreen: contentId=$contentId, contentType=$contentType, loggedInUserId=$loggedInUserId")
                            Toast.makeText(context, "Errore: Dati recensione mancanti.", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                    // *** FIX ENDS HERE ***


                    // Definizione della rotta per la SchedaContenutoCatalogoScreen
                    composable(
                        route = AppScreen.SchedaContenutoCatalogo.route,
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.StringType },
                            navArgument("contentType") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val contentId = backStackEntry.arguments?.getString("contentId")
                        val contentType = backStackEntry.arguments?.getString("contentType")

                        if (contentId != null && contentType != null) {
                            // No need to create a new ViewModel here if it's already instantiated at MainActivity level
                            SchedaContenutoCatalogoScreen(
                                contentId = contentId,
                                contentType = contentType,
                                viewModel = schedaContenutoCatalogoViewModel, // Pass the pre-instantiated ViewModel
                                onBackClick = { navController.popBackStack() },
                                onCatalogoClick = {
                                    navController.navigate(AppScreen.Catalogo.route) {
                                        popUpTo(AppScreen.Catalogo.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onAreaPersonaleClick = { navController.navigate(AppScreen.Utente.route) },
                                onNavigateToSegnalazione = {
                                    navController.navigate(AppScreen.Segnalazione.route)
                                },
                                schedaContenutoPersonaleViewModel = schedaContenutoPersonaleViewModel
                            )
                        } else {
                            Log.e(
                                "NavigationError",
                                "contentId or contentType was null for SchedaContenutoCatalogoScreen"
                            )
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

// ... (AppScreen sealed class remains the same as the previous response) ...

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login_screen")
    object Registrazione : AppScreen("registrazione_screen")

    object Utente : AppScreen("utente_screen")

    object Sezione : AppScreen("sezione/{mediaType}") {
        fun createRoute(mediaType: String): String {
            return "sezione/$mediaType"
        }
    }

    // Personal content detail screen
    object SchedaContenutoPersonale : AppScreen("scheda_contenuto_personale_screen/{contentId}/{contentType}") {
        fun createRoute(contentId: String, contentType: String): String {
            val encodedContentId = URLEncoder.encode(contentId, "UTF-8")
            val encodedContentType = URLEncoder.encode(contentType, "UTF-8")
            return "scheda_contenuto_personale_screen/$encodedContentId/$encodedContentType"
        }
    }

    object Catalogo : AppScreen("catalogo_screen")

    object Segnalazione : AppScreen("segnalazione_screen")

    // IMPORTANT: Updated Recensione route to include arguments
    object Recensione : AppScreen("recensione_screen/{contentId}/{contentType}/{loggedInUserId}") {
        fun createRoute(contentId: String, contentType: String, loggedInUserId: String): String {
            val encodedContentId = URLEncoder.encode(contentId, "UTF-8")
            val encodedContentType = URLEncoder.encode(contentType, "UTF-8")
            val encodedLoggedInUserId = URLEncoder.encode(loggedInUserId, "UTF-8")
            return "recensione_screen/$encodedContentId/$encodedContentType/$encodedLoggedInUserId"
        }
    }

    // Catalog content detail screen
    object SchedaContenutoCatalogo : AppScreen("scheda_contenuto_catalogo_screen/{contentId}/{contentType}") {
        fun createRoute(contentId: String, contentType: String): String {
            val encodedContentId = URLEncoder.encode(contentId, "UTF-8")
            val encodedContentType = URLEncoder.encode(contentType, "UTF-8")
            return "scheda_contenuto_catalogo_screen/$encodedContentId/$encodedContentType"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    loginViewModel: LoginViewModel,
    utenteViewModel: UtenteViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(key1 = loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val loggedInUsername = (loginState as LoginState.Success).username
                utenteViewModel.loadUserByUsername(loggedInUsername)
                onLoginSuccess()
                loginViewModel.resetLoginState()
            }
            is LoginState.Error -> {
                println("Errore Login: ${(loginState as LoginState.Error).message}")
            }
            LoginState.Idle, LoginState.Loading -> {
                // Do nothing, or show a loading indicator
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9EE5C)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(id = R.drawable.seen_logo),
                contentDescription = "Seen Logo",
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tutto ciò che hai visto. E quello che vedrai.",
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Benvenuto",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageVector = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = imageVector, contentDescription = "Toggle password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    loginViewModel.authenticate(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = MaterialTheme.shapes.medium,
                enabled = loginState !is LoginState.Loading
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Login", fontSize = 20.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Non hai un account? ",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                TextButton(onClick = onRegisterClick) {
                    Text(
                        text = "Registrati ora!",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
