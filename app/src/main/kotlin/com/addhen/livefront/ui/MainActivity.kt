// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.addhen.livefront.ui.component.AppSurface
import com.addhen.livefront.ui.navigation.AppNavGraph
import com.addhen.livefront.ui.theme.LivefrontTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LivefrontTheme {
                val navController = rememberNavController()
                AppSurface { AppNavGraph(navController = navController) }
            }
        }
    }
}
