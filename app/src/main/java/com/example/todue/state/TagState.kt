package com.example.todue.state

import com.example.todue.ui.sortType.TagSortType
import com.example.todue.dataLayer.source.local.Tag

data class TagState(

    val tags: List<Tag> = emptyList(),
    val tagsToSort: List<String> = emptyList(),
    val title: String = "",
    val toDoAmount: Int = 0,
    val sort: Boolean = false,
    val isDeletingTag: Boolean = false,
    val tagSortType: TagSortType = TagSortType.TITLE,
    val searchInTags: String = ""

)