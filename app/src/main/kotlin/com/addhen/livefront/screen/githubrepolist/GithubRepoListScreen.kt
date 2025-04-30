// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepolist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.addhen.livefront.R
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.formatStars
import com.addhen.livefront.ui.component.AppScaffold
import com.addhen.livefront.ui.component.ConnectivityStatus
import com.addhen.livefront.ui.component.ErrorInfo
import com.addhen.livefront.ui.component.LoadingIndicator
import com.addhen.livefront.ui.theme.starYellow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@Composable
fun GithubRepoListScreen(
    modifier: Modifier = Modifier,
    viewModel: GithubRepoListViewModel = hiltViewModel(),
    onRepoClick: (repoId: Long) -> Unit,
) {
    val pagingItems = viewModel.searchResults.collectAsLazyPagingItems()

    var showAboutDialog by remember { mutableStateOf(false) }

    AppScaffold(
        title = stringResource(R.string.app_name),
        modifier = modifier,
        actions = {
            IconButton(onClick = { showAboutDialog = true }) {
                Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about_dialog_title))
            }
        },
    ) {
        GithubRepoContent(
            pagingItems = pagingItems,
            onRepoClick = onRepoClick,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalCoroutinesApi::class,
)
@Composable
private fun GithubRepoContent(
    pagingItems: LazyPagingItems<GithubRepo>,
    onRepoClick: (repoId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        ConnectivityStatus()

        LazyColumn {
            items(
                count = pagingItems.itemCount,
                key = { index -> pagingItems[index]?.id ?: index },
            ) { index ->
                pagingItems[index]?.let { repo ->
                    RepositoryItem(
                        repo = repo,
                        onRepoClick = {
                            onRepoClick(repo.id)
                        },
                    )
                }
            }

            pagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingIndicator(modifier = Modifier.fillParentMaxSize()) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { LoadingIndicator() }
                    }
                    loadState.refresh is LoadState.Error -> {
                        Timber.e((loadState.refresh as LoadState.Error).error, "Error occurred while loading")
                        item {
                            ErrorInfo(
                                modifier = Modifier.fillParentMaxSize(),
                                message = stringResource(R.string.error_occurred_while_loading),
                                onRetry = { retry() },
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        Timber.e((loadState.append as LoadState.Error).error, "Error occurred while appending")
                        item {
                            ErrorInfo(
                                message = stringResource(R.string.error_occurred_while_loading),
                                onRetry = { retry() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryItem(
    repo: GithubRepo,
    modifier: Modifier = Modifier,
    onRepoClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onRepoClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = repo.owner.avatarUrl,
                    contentDescription = stringResource(R.string.owner_avatar_image_content_description),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = repo.fullName,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            repo.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = stringResource(R.string.star_icon_content_description),
                        tint = starYellow,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = repo.stargazersCount.formatStars())
                }
            }

            repo.contributor?.let { contributor ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = contributor.avatarUrl,
                        contentDescription = stringResource(R.string.contributor_avatar),
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.top_contributor, contributor.login),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.commits, contributor.contributions),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}
