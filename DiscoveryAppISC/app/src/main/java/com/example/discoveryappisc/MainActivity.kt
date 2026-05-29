package com.example.discoveryappisc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

// 1. Rutas Actualizadas
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Curriculum : Screen("curriculum")
    object Career : Screen("career")
    object Quiz : Screen("quiz")
     object Specialties : Screen("specialties")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ISCAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ISCAppNavigation() {
    val navController = rememberNavController()
    // Empezamos en el Splash Screen
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Curriculum.route) { CurriculumScreen(navController) }
        composable(Screen.Career.route) { CareerScreen(navController) }
        composable(Screen.Quiz.route) { QuizScreen(navController) }
        composable(Screen.Specialties.route) { SpecialtiesScreen(navController) }
    }
}

// 2. Pantalla de Bienvenida (Splash Screen)
@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2500) // Simula la carga de 2.5 segundos
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true } // Evita volver al splash con el botón atrás
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF6200EE), Color(0xFF3700B3)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Logo ISC",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingeniería en Sistemas",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Crea el futuro hoy",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }
    }
}

// 3. Pantalla Principal Modificada (Home)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
     // Necesario para lanzar el Intent hacia WhatsApp
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Descubre ISC", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
         // AQUÍ AGREGAMOS EL BOTÓN FLOTANTE (FAB)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Intent implícito hacia WhatsApp
                    val phoneNumber = "5212721234567" // Número de admisiones (puedes ajustarlo luego)
                    val message = "¡Hola! Descargué la app Descubre ISC y me gustaría recibir más información sobre la carrera y el proceso de admisión."
                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF25D366), // Color verde de WhatsApp
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Contacto Admisiones")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { LottieHeader() }

            // Botón destacado para el Quiz Vocacional
            item {
                QuizBannerCard(onClick = { navController.navigate(Screen.Quiz.route) })
            }

            item {
                InfoCard(
                    title = "¿Qué hacemos?",
                    desc = "Programamos apps, diseñamos inteligencias artificiales, protegemos redes de hackers y creamos la tecnología que mueve al mundo.",
                    color = Color(0xFF6200EE)
                )
            }

            item {
                MenuButton(
                    title = "Plan de Estudios",
                    icon = Icons.Default.MenuBook,
                    onClick = { navController.navigate(Screen.Curriculum.route) }
                )
            }
            item {
                MenuButton(
                    title = "Campo Laboral",
                    icon = Icons.Default.WorkOutline,
                    onClick = { navController.navigate(Screen.Career.route) }
                )
            }

            // Sección para el Código QR
            item {
                QRCodeSection()
            }
        }
    }
}

// 4. Nuevo Quiz Interactivo
@Composable
fun QuizScreen(navController: NavController) {
    val questions = listOf(
        "¿Te gusta resolver acertijos y problemas lógicos?",
        "¿Te da curiosidad saber cómo funcionan las apps que usas a diario?",
        "¿Te imaginas liderando un equipo de desarrollo tecnológico?",
        "¿Te frustras poco cuando algo no funciona a la primera y prefieres buscar la solución?"
    )

    var currentQuestion by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showResult) {
            LinearProgressIndicator(
                progress = { (currentQuestion + 1) / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Color(0xFF6200EE)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Pregunta ${currentQuestion + 1} de ${questions.size}",
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = questions[currentQuestion],
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    score += 1
                    if (currentQuestion < questions.size - 1) currentQuestion++ else showResult = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("¡Sí, totalmente!", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    if (currentQuestion < questions.size - 1) currentQuestion++ else showResult = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("No mucho", fontSize = 18.sp, color = Color.DarkGray)
            }
        } else {
            // Pantalla de Resultados
            Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Tu compatibilidad con ISC es:", fontSize = 20.sp, color = Color.Gray)
            Text("${(score.toFloat() / questions.size * 100).toInt()}%", fontSize = 60.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (score >= 3) "¡Tienes madera de Ingeniero/a en Sistemas! Tu perfil analítico y curioso encaja perfecto." else "Quizás tengas otros intereses, ¡pero la tecnología siempre será una gran herramienta para ti!",
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Inicio")
            }
        }
    }
}

// --- Componentes UI Mejorados ---

@Composable
fun QuizBannerCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(Color(0xFFFF512F), Color(0xFFDD2476))))
                .padding(24.dp)
        ) {
            Column {
                Text("¿Naciste para programar?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Haz nuestro test vocacional de 1 minuto y descúbrelo.", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Iniciar Test", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun QRCodeSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Descarga el Plan de Estudios", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Escanea este código para llevarte el PDF.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            // Placeholder para el QR (Aquí podrías poner un componente de imagen real)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", modifier = Modifier.size(100.dp))
            }
        }
    }
}

// Componentes conservados (con ligeros ajustes de diseño)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan de Estudios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Tu ruta como Ingeniero",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val materias = listOf(
                "Programación y Algoritmia" to Icons.Default.Code,
                "Bases de Datos" to Icons.Default.Storage,
                "Inteligencia Artificial" to Icons.Default.Psychology,
                "Redes y Seguridad" to Icons.Default.Security,
                "Ingeniería de Software" to Icons.Default.DeveloperBoard
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(materias.size) { index ->
                    val (materia, icon) = materias[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF6200EE).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color(0xFF6200EE))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(materia, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campo Laboral", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿Dónde puedes trabajar?",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF333333)
            )

            // Tarjetas de roles laborales
            JobRoleCard(
                title = "Desarrollo de Software",
                desc = "Creación de apps móviles, plataformas web y software empresarial.",
                icon = Icons.Default.Smartphone,
                color = Color(0xFF03DAC5)
            )
            JobRoleCard(
                title = "Arquitectura Cloud y Redes",
                desc = "Diseño de infraestructuras en la nube y seguridad informática.",
                icon = Icons.Default.Cloud,
                color = Color(0xFF2196F3)
            )
            JobRoleCard(
                title = "Gestión de Proyectos TI",
                desc = "Liderazgo en células de desarrollo ágil y consultoría tecnológica.",
                icon = Icons.Default.Group,
                color = Color(0xFFFF9800)
            )
            JobRoleCard(
                title = "Emprendimiento y Remoto",
                desc = "Crea tu propia startup o trabaja para empresas internacionales desde Veracruz o cualquier parte del mundo.",
                icon = Icons.Default.Public,
                color = Color(0xFFE91E63)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun JobRoleCard(title: String, desc: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
            }
        }
    }
}

// --- Componentes Reutilizables Mejorados ---

 // Asegúrate de tener esta importación

@Composable
fun LottieHeader() {
    // Implementación real con Lottie.
    // Necesitas descargar un archivo .json de Lottiefiles.com (ej. "tech_animation.json")
    // y colocarlo en la carpeta res/raw/ de tu proyecto Android.

    /* DESCOMENTA ESTO CUANDO TENGAS TU ARCHIVO LOTTIE EN res/raw/animacion_isc.json
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_isc))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Animación en bucle
    )

    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )
    }
    */

    // MIENTRAS TANTO, usaré una animación nativa de Compose muy atractiva simulando "código flotante":
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "offset"
    )

    Box(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.DeveloperMode,
            contentDescription = "Animación",
            modifier = Modifier
                .size(90.dp)
                .offset(y = offsetY.dp),
            tint = Color(0xFF6200EE)
        )
    }
}

@Composable
fun InfoCard(title: String, desc: String, color: Color) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Diseño flat (plano) moderno
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF333333))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(desc, fontSize = 15.sp, color = Color.DarkGray, lineHeight = 22.sp)
        }
    }
}

@Composable
fun MenuButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF6200EE),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}
