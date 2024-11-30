package com.example.autenticacaobiometria

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.autenticacaobiometria.ui.theme.AutenticacaoBiometriaTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutenticacaoBiometriaTheme {
               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   val biometricResult by promptManager.promptResults.collectAsState(initial = null )

                   val enrollLauncher = rememberLauncherForActivityResult(
                       contract = ActivityResultContracts.StartActivityForResult(),
                       onResult = {
                           println("Activity result: $it")
                       }
                   )
                   LaunchedEffect(biometricResult){
                       if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet ){
                           if (Build.VERSION.SDK_INT >= 30){
                               val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                   putExtra(
                                       Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL

                                   )
                               }

                               enrollLauncher.launch(enrollIntent)
                           }
                       }
                   }

                   Column (
                       modifier = Modifier
                           .fillMaxSize(),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment =  Alignment.CenterHorizontally
                   ){
                       Button(onClick = {
                           promptManager.showBiometricPrompt(
                            title = "prompt Simples",
                           description = "descricao simples prompt"
                           )
                       }) {
                           Text(text = "Autenticado")
                       }
                       biometricResult?.let { result ->
                           Text(
                               text = when(result) {
                                   is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                       result.error
                                   }
                                   BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                       "Authentication failed"
                                   }
                                   BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                       "Authentication not set"
                                   }
                                   BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                                       "Authentication success"
                                   }
                                   BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                       "Feature unavailable"
                                   }
                                   BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                       "Hardware unavailable"
                                   }

                                   else -> "Unknown error"
                               }
                           )
                       }
                   }

               }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutenticacaoBiometriaTheme {
        Greeting("Android")
    }
}