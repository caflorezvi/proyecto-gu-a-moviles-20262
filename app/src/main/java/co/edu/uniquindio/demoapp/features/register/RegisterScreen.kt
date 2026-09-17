package co.edu.uniquindio.demoapp.features.register

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


@Composable
fun RegisterScreen() {

    // Estado para los campos de entrada, remember mantiene el estado entre recomposiciones
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = CenterVertically)
    ) {
        OutlinedTextField(
            value = email, // Estado del campo de email
            onValueChange = {
                email = it

                emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()

            }, // Actualiza el estado cuando el usuario escribe.
            label = {
                Text(text = "Email")
            },
            isError = emailError,
            supportingText = {
                if(emailError) {
                    Text(
                        text = "Por favor ingrese un email válido"
                    )
                }
            }
        )
        OutlinedTextField(
            value = password, // Estado del campo de password
            onValueChange = {
                password = it

                if(password.length !in 6..15){
                    passwordError = true
                } else {
                    passwordError = false
                }

            }, // Actualiza el estado cuando el usuario escribe
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text(text = "Password")
            },
            isError = passwordError,
            supportingText = {
                if(passwordError) {
                    Text(
                        text = "Por favor ingrese un password válido"
                    )
                }
            }
        )
        Button(
            onClick = {

                // Se imprime el email y password en el logcat
                Log.d("Login", "Email: $email, Password: $password")
            },
            enabled = !passwordError && !emailError && email.isNotEmpty() && password.isNotEmpty(),
            content = {
                Text(text = "Iniciar Sesión")
            }
        )

    }
}