package com.addhen.livefront.screen.githubrepodetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.addhen.livefront.R
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.GithubRepo.Contributor
import com.addhen.livefront.screen.githubrepodetail.GithubRepoDetailViewModel.RepoDetailUiState
import com.addhen.livefront.ui.component.AppScaffold
import com.addhen.livefront.ui.component.ErrorInfo
import com.addhen.livefront.ui.component.LoadingIndicator

@Composable
fun GithubRepoDetailScreen(
    viewModel: GithubRepoDetailViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val githubRepo by viewModel.githubRepo.collectAsStateWithLifecycle()

    AppScaffold(
        title = stringResource(R.string.app_name),
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_icon_content_description)) } },
    ) {
        GithuRepoDetailContent(
            githubRepo = githubRepo,
            uiState = uiState,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GithuRepoDetailContent(
    githubRepo: GithubRepo?,
    uiState: RepoDetailUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .then(modifier),
    ) {
        when {
            uiState.isLoadingRepo -> LoadingIndicator()
            uiState.error != null -> ErrorInfo(message = uiState.error) {}
            githubRepo != null -> {
                GithuRepoDetailContent(
                    modifier = modifier,
                    repo = githubRepo!!,
                )
            }

        }
    }
}


@Composable
fun GithuRepoDetailContent(
    repo: GithubRepo,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current


   Column(
        modifier = modifier.then(Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(repo.owner.avatarUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .build(),
                contentDescription = "${repo.owner.login} avatar",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatStars(repo.stargazersCount),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = repo.description ?: "No description available.",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Link to GitHub
        Button(onClick = { uriHandler.openUri(repo.htmlUrl) }) {
            Text("View on GitHub")
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Contributors Section
        Text(
            text = "Top Contributors",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

       when {
           repo.contributors.isNotEmpty() -> {
               LazyRow(
                   horizontalArrangement = Arrangement.spacedBy(12.dp),
                   contentPadding = PaddingValues(vertical = 8.dp)
               ) {
                   items(repo.contributors.size) { index ->
                       ContributorItem(repo.contributors[index])
                   }
               }
           } else -> {
               Text("No contributors found.")
           }
       }
    }
}

@Composable
fun ContributorItem(
    contributor: Contributor,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp) // Fixed width for items in LazyRow
            .clickable { uriHandler.openUri(contributor.htmlUrl) }
            .padding(vertical = 4.dp)
            .then(modifier)

    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(contributor.avatarUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .build(),
            contentDescription = "${contributor.login} avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = contributor.login,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            text = "${contributor.contributions} commits",
            fontSize = 10.sp,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

fun formatStars(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
        else -> count.toString()
    }
}