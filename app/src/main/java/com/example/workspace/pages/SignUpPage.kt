package com.example.workspace.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workspace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpPage(): ComponentActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            MainScreen(
                Modifier
                    .fillMaxSize()
                    .background(color = Color.Black),
                auth = auth
            )
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, auth: FirebaseAuth) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.tasks),
                contentDescription = "Logo of tasks app",
                modifier = Modifier
                    .size(45.dp)
                    .offset(x = 55.dp, y = 20.dp)
            )
            Text(
                text = "Workspace",
                color = Color.Black,
                fontSize = 40.sp,
                letterSpacing = 5.sp,
                style = TextStyle(
                    shadow = Shadow(color = Color.Black, offset = Offset(1f,1f), blurRadius = 2f)
                ),
                fontFamily = FontFamily.Cursive,
                modifier = Modifier.padding(start = 65.dp, top = 20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Home Screen Image",
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Sign Up",
            color = Color.Black,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(color = Color.White, offset = Offset(1f, 1f), blurRadius = 2f)
            ),
            modifier = Modifier
                .padding(start = 10.dp, top = 3.dp)
                .offset(x = 100.dp)
        )

        Column(modifier = Modifier
            .fillMaxSize(0.85f)
            .offset(x = 30.dp),
            verticalArrangement = Arrangement.Center) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .padding(8.dp),
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build()

                                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                                            if (profileTask.isSuccessful) {
                                                Toast.makeText(context, "Sign Up Done Successfully", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(context, HomeActivity::class.java)
                                                context.startActivity(intent)
                                                (context as? Activity)?.finish()
                                            } else {
                                                Toast.makeText(context, "Failed to save username: ${profileTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Fill Details Properly", Toast.LENGTH_SHORT).show()
                        }

                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Sign Up",
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Serif
                    )

                }


            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainPreview() {
    MainScreen(auth = FirebaseAuth.getInstance())
}

