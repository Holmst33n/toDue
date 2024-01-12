package com.example.todue.ui.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todue.dataLayer.source.local.ToDo
import com.example.todue.dataLayer.source.local.ToDoRepository
import com.example.todue.state.ToDoState
import com.example.todue.ui.event.ToDoEvent
import com.example.todue.ui.sortType.ToDoSortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ToDosViewModel(
    private val toDoRepository: ToDoRepository
): ViewModel() {
    private val toDoSortType = MutableStateFlow(ToDoSortType.DUE_DATE)
    private val search = MutableStateFlow("")
    private val selectedCalendarDate = MutableStateFlow("")
    private val showFinished = MutableStateFlow(false)

    private var sortInt = 0

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _toDos = toDoSortType
        .flatMapLatest { toDoSortType ->
            when(toDoSortType) {
                ToDoSortType.TAG -> toDoRepository.getToDosOrderedByTags(search.value, showFinished.value)
                ToDoSortType.PLACEHOLDER -> toDoRepository.getToDosOrderedByDueDate(search.value, showFinished.value)
                ToDoSortType.DUE_DATE -> toDoRepository.getToDosOrderedByDueDate(search.value, showFinished.value)
                ToDoSortType.GIVEN_DATE -> toDoRepository.getToDosByGivenDate(selectedCalendarDate.value)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _toDoState = MutableStateFlow(ToDoState())
    val toDoState = combine(_toDoState, toDoSortType, _toDos){ toDoState, toDoSortType, toDos ->
        toDoState.copy(
            toDos = toDos,
            toDoSortType = toDoSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToDoState())

    fun onEvent(toDoEvent: ToDoEvent) {

        when(toDoEvent) {

            is ToDoEvent.CreateToDo -> {

                val title = toDoState.value.title
                val description = toDoState.value.description
                val tag = toDoState.value.tag
                val dueDate = toDoState.value.dueDate
                val dueTime = toDoState.value.dueTime
                val finished = toDoState.value.finished

                if (title.isEmpty()) {
                    return
                }

                val toDoObject = ToDo(
                    title = title,
                    description = description,
                    tag = tag,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    finished = finished
                )

                viewModelScope.launch{
                    toDoRepository.createToDo(
                        toDoObject.title,
                        toDoObject.description,
                        toDoObject.tag,
                        toDoObject.dueDate,
                        toDoObject.dueTime,
                        toDoObject.finished
                    )
                }

                _toDoState.update { it.copy(
                    isCreatingToDo = false,
                    isDeletingToDo = false,
                    isEditingToDo = false,
                    title = "",
                    description = "",
                    tag = "",
                    dueDate = "",
                    dueTime = "",
                    finished = false
                ) }

            }

            is ToDoEvent.DeleteToDo -> {
                viewModelScope.launch {
                    toDoRepository.deleteToDo(toDoEvent.toDo)
                }
            }

            is ToDoEvent.HideCreateDialog -> {
                _toDoState.update {it.copy(
                    isCreatingToDo = false
                ) }
            }

            is ToDoEvent.SetDescription -> {
                _toDoState.update {it.copy(
                    description = toDoEvent.description
                ) }
            }

            is ToDoEvent.SetDueDate -> {
                _toDoState.update {it.copy(
                    dueDate = toDoEvent.dueDate
                ) }
            }

            is ToDoEvent.SetDueTime -> {
                _toDoState.update {it.copy(
                    dueTime = toDoEvent.dueTime
                )}
            }

            is ToDoEvent.FinishToDo -> {
                viewModelScope.launch {
                    toDoRepository.finishToDo(toDoEvent.toDo)
                    _toDoState.update { it.copy(
                        isFinishingToDo = true
                    ) }
                }
            }

            is ToDoEvent.UnFinishToDo -> {
                viewModelScope.launch {
                    toDoRepository.unFinishToDo(toDoEvent.toDo)
                }
            }

            is ToDoEvent.SetTag -> {
                _toDoState.update {it.copy(
                    tag = toDoEvent.tag
                ) }
            }

            is ToDoEvent.SetTitle -> {
                _toDoState.update {it.copy(
                    title = toDoEvent.title
                ) }
            }

            is ToDoEvent.ShowCreateDialog -> {
                _toDoState.update {it.copy(
                    isCreatingToDo = true
                ) }
            }

            is ToDoEvent.ShowDeleteToDoDialog -> {
                _toDoState.update {it.copy(
                    isDeletingToDo = true
                ) }
            }

            is ToDoEvent.HideDeleteToDoDialog -> {
                _toDoState.update {it.copy(
                    isDeletingToDo = false
                ) }
            }

            is ToDoEvent.ShowToDoDialog -> {
                _toDoState.update {it.copy(
                    isCheckingToDo = true
                ) }
            }

            is ToDoEvent.HideToDoDialog -> {
                _toDoState.update {it.copy(
                    isCheckingToDo = false
                ) }
            }

            is ToDoEvent.AddTagToSortToDos -> {
                sortInt++
                toDoSortType.value = ToDoSortType.PLACEHOLDER
                toDoSortType.value = ToDoSortType.TAG
            }

            is ToDoEvent.RemoveTagToSortToDos -> {
                sortInt--
                toDoSortType.value = ToDoSortType.DUE_DATE
                if (sortInt > 0) {
                    toDoSortType.value = ToDoSortType.TAG
                }
            }

            is ToDoEvent.SetSortToDosByFinished -> {
                val temp = toDoSortType.value
                toDoSortType.value = ToDoSortType.PLACEHOLDER
                toDoSortType.value = temp
                showFinished.value = toDoEvent.finished
                _toDoState.update {it.copy(
                    sortByFinished = toDoEvent.finished
                )}
            }

            is ToDoEvent.SortToDosByDueDate -> {
                toDoSortType.value = ToDoSortType.DUE_DATE
            }

            is ToDoEvent.DeleteTagFromToDos -> {
                viewModelScope.launch {
                    toDoRepository.deleteTagFromToDos(toDoEvent.tag)
                }
            }

            is ToDoEvent.ShowEditToDoDialog -> {
                _toDoState.update {it.copy(
                    isEditingToDo = true
                )}
            }

            is ToDoEvent.HideEditToDoDialog -> {
                _toDoState.update {it.copy(
                    isEditingToDo = false
                )}
            }

            is ToDoEvent.EditToDo -> {

                viewModelScope.launch {
                    toDoRepository.editToDo(newTitle = toDoEvent.newTitle, newDescription = toDoEvent.newDescription, newTag = toDoEvent.newTag, newDueDate = toDoEvent.newDueDate, newDueTime = toDoEvent.newDueTime, toDoId = toDoEvent.toDoId)
                }

                _toDoState.update {
                    it.copy(
                        title = "",
                        description = "",
                        tag = "",
                        dueDate = "",
                        dueTime = "",
                        isEditingToDo = false,
                        isCheckingToDo = false
                    )
                }
            }

            is ToDoEvent.SetToDoStateForEdit -> {
                _toDoState.update {
                    it.copy(
                        title = toDoEvent.toDo.title,
                        description = toDoEvent.toDo.description,
                        tag = toDoEvent.toDo.tag,
                        dueDate = toDoEvent.toDo.dueDate,
                        dueTime = toDoEvent.toDo.dueTime
                    )
                }
            }

            is ToDoEvent.ResetToDoState -> {
                _toDoState.update { it.copy(
                    isCreatingToDo = false,
                    isDeletingToDo = false,
                    isEditingToDo = false,
                    isFinishingToDo = false,
                    title = "",
                    description = "",
                    tag = "",
                    dueDate = "",
                    dueTime = "",
                    finished = false
                ) }
            }

            is ToDoEvent.SetSearchInToDos -> {
                _toDoState.update { it.copy(
                    searchInToDos = toDoEvent.searchInToDos
                )}
                if(toDoSortType.value == ToDoSortType.TAG) {
                    toDoSortType.value = ToDoSortType.DUE_DATE
                    toDoSortType.value = ToDoSortType.TAG
                } else {
                    toDoSortType.value = ToDoSortType.TAG
                    toDoSortType.value = ToDoSortType.DUE_DATE
                }
                search.value = toDoEvent.searchInToDos
            }

            is ToDoEvent.SortToDosByGivenDate -> {
                toDoSortType.value = ToDoSortType.DUE_DATE
                selectedCalendarDate.value = toDoEvent.date
                toDoSortType.value = ToDoSortType.GIVEN_DATE
            }

            //only to avoid creating a bunch of todos when testing
            is ToDoEvent.PopulateToDoList -> {
                viewModelScope.launch{
                    toDoRepository.createToDo(
                        "Todo CRUD",
                        "Todos with title, description, tag, completion status, due date and due time. They " +
                                "must be editable, deletable and finishable.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Grouping the todos",
                        "Group todos by due date at the least.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Search",
                        "Let users search for todos based on title, description and tag.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Filtering",
                        "Filter todos by completion status and/or tags added.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Use Giphy API",
                        "Show a 'celebration' gif when a todo is finished.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Calendar",
                        "Implement a calendar where users can see what todos are due on a given date.",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    delay(10L)
                    toDoRepository.createToDo(
                        "Statistics",
                        "Show the user some statistics for a bit of gamification",
                        "Final delivery",
                        "2024-01-17",
                        "15:00",
                        false
                    )
                    toDoRepository.createToDo(
                        "Clean the kitchen",
                        "Oven and fridge are really dirty",
                        "Cleaning",
                        "2024-01-18",
                        "21:00",
                        false
                    )
                    toDoRepository.createToDo(
                        "Mop the floor",
                        "It's dirty after someone walked around inside with their shoes on",
                        "Cleaning",
                        "2024-01-18",
                        "21:00",
                        false
                    )
                }
            }

            is ToDoEvent.GetStatistics -> {
                viewModelScope.launch {
                    _toDoState.update {
                        it.copy(
                            totalAmountOfCreatedToDos = toDoRepository.getTotalAmountOfCreatedToDos(),
                            totalAmountOfFinishedToDos = toDoRepository.getTotalAmountOfFinishedToDos(),
                            totalAmountOfUnfinishedToDos = toDoRepository.getTotalAmountOfUnfinishedToDos()
                        )
                    }
                }
            }
        }
    }
}