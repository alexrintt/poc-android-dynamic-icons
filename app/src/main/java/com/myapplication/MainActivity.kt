package com.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = {
                                setCurrentAppIcon(iconToEnable = AppIcons.RED)
                            }
                        ) {
                            Text("Change Icon To Red")
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            // Aqui eu to trocando o icone ao clicar nos botões pra simplificar,
                            // mas você pode fazer isso rodando algum código em background/periodicamente
                            // que verifica se é data X, Y ou Z e muda o icone de acordo.
                            onClick = {
                                setCurrentAppIcon(iconToEnable = AppIcons.BLUE)
                            }
                        ) {
                            Text("Change Icon To Blue")
                        }
                    }
                }
            }
        }
    }

    // Nesse caso o context é a própria activity,
    // mas existem N formas de pegar o contexto dependendo de onde você tá mexendo
    private val context: Context
        get() {
            return this@MainActivity
        }

    private fun disableComponentName(componentName: String) {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, componentName),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun enableComponentName(componentName: String) {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, componentName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun setCurrentAppIcon(iconToEnable: AppIcons) {
        // Se você chamar essa função logo após setar o novo icone
        // o app vai fechar pois você vai desabilitar o componentName atual
        // É a única maneira de manter o exemplo simples.
        // Pra evitar esse problema teríamos que tentar algumas outras gambiarras mais complexas kk
        setupAppIcon(iconToEnable)
    }

    // Provavelmente, tem que chamar essa função quando o app está em background / pausado ou em algum estado que o usuário não perceba que o app "crashou"
    private fun setupAppIcon(iconToEnable: AppIcons) {
        val enabledIconComponentName = iconToEnable.componentName

        // Habilita o icone selecionado
        enableComponentName(enabledIconComponentName)

        // E desabilita o resto
        val disabledIcons = AppIcons.entries.filter {
            it.componentName != enabledIconComponentName
        }

        for (disabledIcon in disabledIcons) {
            disableComponentName(disabledIcon.componentName)
        }
    }
}

enum class AppIcons(val componentName: String) {
    // No Fluter esse componentName pode ser uma variável enviada pro plugin, algo como:
    // ```dart
    // AndroidDynamicAppIcons.setCurrentEnabledIcon(id: "com.myapplication.MainActivityRed")
    // ```
    RED("com.myapplication.MainActivityRed"),
    BLUE("com.myapplication.MainActivityBlue");

    companion object {
        fun fromComponentName(componentName: String): AppIcons? {
            return AppIcons.entries.firstOrNull { it.componentName == componentName }
        }
    }
}