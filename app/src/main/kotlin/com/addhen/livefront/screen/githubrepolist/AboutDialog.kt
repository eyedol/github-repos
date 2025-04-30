// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.addhen.livefront.BuildConfig
import com.addhen.livefront.R

@Composable
fun AboutDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    val appVersion = BuildConfig.VERSION_NAME

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(modifier),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = R.mipmap.ic_launcher_round,
                    contentDescription = stringResource(R.string.app_logo_content_description),
                    modifier = Modifier.size(80.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.version, appVersion), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.app_description), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}
