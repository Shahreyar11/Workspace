package com.example.workspace.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workspace.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.ui.graphics.Shape


class HomeActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            val intent = Intent(this@HomeActivity, LoginPage::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            MainScreen()
        }
    }

    @RequiresApi(35)
    @Composable
    fun MainScreen() {
        var selectedScreen by remember { mutableStateOf("Home") }

        Scaffold(
            topBar = { AppTopBar() },
            bottomBar = { AppBottomNavigation(selectedScreen) { selectedScreen = it } }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedScreen) {
                    "Home" -> HomeContent()
                    "Analyze" -> AnalyzeScreen()
                    "Focus" -> FocusScreen()
                    "Profile" -> ProfileScreen()
                }
            }
        }
    }

    @Composable
    fun AppTopBar() {
        val auth = FirebaseAuth.getInstance()
        val userName = auth.currentUser?.displayName ?: "User"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), clip = false)
                .background(color = Color(0xFFFFA726), shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tasks),
                    contentDescription = "Logo of tasks app",
                    modifier = Modifier
                        .size(45.dp)
                        .shadow(elevation = 1.dp)
                )

                Text(
                    text = "   Hi, $userName",
                    fontSize = 35.sp,
                    fontFamily = FontFamily.Cursive,
                    color = Color.Black,
                    style = TextStyle(
                        shadow = Shadow(color = Color.Black, offset = Offset(1f, 1f))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )

                val context = LocalContext.current
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            FirebaseAuth
                                .getInstance()
                                .signOut()
                            Toast
                                .makeText(context, "Logged out successfully", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(context, LoginPage::class.java)
                            context.startActivity(intent)
                            (context as ComponentActivity).finish()
                        }
                )
            }
        }
    }


    @Composable
    fun AppBottomNavigation(selectedScreen: String, onScreenSelected: (String) -> Unit) {
        NavigationBar(
            containerColor = Color(0xFFFAFAFA),
            contentColor = Color.Black,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                selected = selectedScreen == "Home",
                onClick = { onScreenSelected("Home") },
                icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = "Home", modifier = Modifier.size(30.dp)) },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selectedScreen == "Analyze",
                onClick = { onScreenSelected("Analyze") },
                icon = { Icon(painterResource(R.drawable.ic_analyze), contentDescription = "Analyze",modifier = Modifier.size(30.dp)) },
                label = { Text("Analyze") }
            )
            NavigationBarItem(
                selected = selectedScreen == "Focus",
                onClick = { onScreenSelected("Focus") },
                icon = { Icon(painterResource(R.drawable.ic_focus), contentDescription = "Focus",modifier = Modifier.size(30.dp)) },
                label = { Text("Focus") }
            )
            NavigationBarItem(
                selected = selectedScreen == "Profile",
                onClick = { onScreenSelected("Profile") },
                icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Profile",modifier = Modifier.size(30.dp)) },
                label = { Text("Profile") }
            )
        }
    }

    @Composable
    fun HomeContent() {
        var date by remember { mutableStateOf(getCurrentDate()) }
        var time by remember { mutableStateOf(getCurrentTime()) }
        val tasks = remember { mutableStateListOf("") }
        val checkedStates = remember { mutableStateListOf(false) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000L)
                val currentDate = getCurrentDate()
                if (currentDate != date) {
                    date = currentDate
                }
                time = getCurrentTime()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = date,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Time",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = time,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 56.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing between tasks
                        ) {
                            items(tasks.size) { index ->

                                if (checkedStates.size <= index) {
                                    checkedStates.add(false)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FiberManualRecord,
                                        contentDescription = "Bullet point",
                                        modifier = Modifier.size(8.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    BasicTextField(
                                        value = tasks[index],
                                        onValueChange = { newValue ->
                                            tasks[index] = newValue
                                            if (index == tasks.size - 1 && newValue.isNotEmpty()) {
                                                tasks.add("")
                                                checkedStates.add(false)
                                            }
                                        },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(8.dp)
                                    )

                                    Checkbox(
                                        checked = checkedStates[index],
                                        onCheckedChange = { checked ->
                                            checkedStates[index] = checked
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }


            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Add Tasks",
                modifier = Modifier
                    .size(85.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color.Black)
                    .clickable {

                    }

            )

        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }




    @Composable
    fun AnalyzeScreen() {
        Box(modifier = Modifier.background(Color.Cyan))
    }
 
    @RequiresApi(35)
    @Composable
    fun FocusScreen() {
        var elapsedTime by remember { mutableStateOf(0L) }
        var isRunning by remember { mutableStateOf(false) }
        val recordedTimes = remember { mutableStateListOf<String>() }


        LaunchedEffect(isRunning) {
            if (isRunning) {
                while (isRunning) {
                    delay(1000L) // Increment every second.
                    elapsedTime++
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .background(Color.Yellow),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = formatElapsedTime(elapsedTime),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Image(
                        painter = painterResource(id = R.drawable.play),
                        contentDescription = "Focus Button",
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                isRunning = !isRunning
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (elapsedTime > 0) {
                        recordedTimes.add(0, formatElapsedTime(elapsedTime))
                        if (recordedTimes.size > 5) recordedTimes.removeLast()
                    }
                    elapsedTime = 0L
                    isRunning = false
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset")
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                items(recordedTimes.size) { index ->
                    Text(
                        text = "${index + 1}. ${recordedTimes[index]}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }

    
    fun formatElapsedTime(elapsedTime: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(elapsedTime)
        val seconds = elapsedTime % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    @Composable
    fun ProfileScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Magenta),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Your Profile", fontSize = 20.sp, color = Color.Black)
        }
    }

    @RequiresApi(35)
    @Preview(showSystemUi = true)
    @Composable
    fun MainPreview() {
        MainScreen()
    }
}
