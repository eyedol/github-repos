
package com.addhen.livefront.screen.githubrepolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.addhen.livefront.data.respository.GithubRepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GithubRepoListViewModel @Inject constructor(
    private val repository: GithubRepoRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("stars:>0")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults = searchQuery
        .debounce(300L)
        .filterNot { it.isEmpty() }
        .flatMapLatest { query -> repository.searchRepos(query) }
        .cachedIn(viewModelScope)
}
