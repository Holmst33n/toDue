package com.example.todue.ui.screens.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todue.dataLayer.source.local.TagRepository
import com.example.todue.state.TagState
import com.example.todue.ui.event.TagEvent
import com.example.todue.ui.sortType.TagSortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagsViewModel(
    private val tagRepository: TagRepository
): ViewModel() {
    private val tagSortType = MutableStateFlow(TagSortType.TITLE)
    private val search = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tags = tagSortType
        .flatMapLatest { tagSortType ->
            when(tagSortType) {
                TagSortType.TITLE -> tagRepository.getTagsOrderedByTitle(search.value)
                TagSortType.PLACEHOLDER -> tagRepository.getTagsOrderedByTitle(search.value)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _tagState = MutableStateFlow(TagState())
    val tagState = combine(_tagState, tagSortType, _tags){ tagState, tagSortType, tags ->
        tagState.copy(
            tags = tags,
            tagSortType = tagSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TagState())

    fun onEvent(tagEvent: TagEvent) {

        when(tagEvent) {

            is TagEvent.CreateTag -> {

                viewModelScope.launch{
                    tagRepository.createTag(
                        tagEvent.title,
                        toDoAmount = 1,
                        sort = false
                    )
                }

                _tagState.update { it.copy(
                    title = "",
                    toDoAmount = 0,
                    sort = false,
                ) }

            }

            is TagEvent.DeleteTag -> {
                viewModelScope.launch {
                    tagRepository.dontSortByThisTag(tagEvent.title)
                    tagRepository.deleteTag(tagEvent.title)
                }
            }

            is TagEvent.ShowDeleteDialog -> {
                _tagState.update {it.copy(
                    isDeletingTag = true
                ) }
            }

            is TagEvent.HideDeleteDialog -> {
                _tagState.update {it.copy(
                    isDeletingTag = false
                ) }
            }

            is TagEvent.SetTitle -> {
                _tagState.update {it.copy(
                    title = tagEvent.title
                ) }
            }

            is TagEvent.IncreaseToDoAmount -> {
                _tagState.update {it.copy(
                    toDoAmount = it.toDoAmount+1
                ) }
            }

            is TagEvent.DecreaseToDoAmount -> {
                viewModelScope.launch {
                    tagRepository.decreaseToDoAmount(tagEvent.title)
                    tagRepository.deleteUnusedTags()
                }
            }

            is TagEvent.SortByThisTag -> {
                viewModelScope.launch {
                    tagRepository.sortByThisTag(tag = tagEvent.tag)
                }
            }

            is TagEvent.DontSortByThisTag -> {
                viewModelScope.launch {
                    tagRepository.dontSortByThisTag(tagTitle = tagEvent.title)
                }
            }

            is TagEvent.ResetTagSort -> {
                viewModelScope.launch {
                    tagRepository.resetTagSort()
                }
            }

            is TagEvent.SetSearchInTags -> {
                _tagState.update { it.copy(
                    searchInTags = tagEvent.searchInTags
                )}
                tagSortType.value = TagSortType.PLACEHOLDER
                tagSortType.value = TagSortType.TITLE
                search.value = tagEvent.searchInTags
            }
        }
    }
}