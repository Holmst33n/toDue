@file:Suppress("UNCHECKED_CAST")

package com.example.todue.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todue.ui.theme.ToDoTheme
import com.example.todue.di.DatabaseModules
import com.example.todue.ui.stateholders.TagsViewModel
import com.example.todue.ui.stateholders.ToDosViewModel
import com.example.todue.ui.stateholders.CalendarViewModel
import com.example.todue.ui.screens.GeneralLayout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.todue.dataLayer.source.local.TagRepository
import com.example.todue.dataLayer.source.local.ToDoRepository
import com.example.todue.ui.event.TagEvent
//import com.example.todue.ui.theme.DarkThemeProvider

class MainActivity : ComponentActivity() {

    private val toDosViewModel by viewModels<ToDosViewModel>( // ViewModels to manage the state of the to-do lists.
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ToDosViewModel(
                        ToDoRepository(DatabaseModules.provideToDoDao(DatabaseModules.provideDataBase(applicationContext)))
                    ) as T
                }
            }
        }
    )

    private val tagsViewModel by viewModels<TagsViewModel>( // ViewModels to manage the state of the tags.
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TagsViewModel(
                        TagRepository(DatabaseModules.provideTagDao(DatabaseModules.provideDataBase(applicationContext)))
                    ) as T
                }
            }
        }
    )

    private val calendarViewModel by viewModels<CalendarViewModel>( // ViewModels to manage the state of the tags.
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CalendarViewModel(
                        ToDoRepository(DatabaseModules.provideToDoDao(DatabaseModules.provideDataBase(applicationContext)))
                    ) as T
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) { // Collect the state of the to-do list and tags into composable functions.
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTheme(useDarkTheme = isSystemInDarkTheme()) {
                val toDoState by toDosViewModel.toDoState.collectAsState()
                val tagState by tagsViewModel.tagState.collectAsState()
                val systemUiController = rememberSystemUiController() // Control the system UI bars.
                val useDarkIcons = !isSystemInDarkTheme()
                val onTagEvent = tagsViewModel::onEvent
                val onToDoEvent = toDosViewModel::onEvent
                val onCalendarEvent = calendarViewModel::onEvent
                val calendarState by calendarViewModel.calendarState.collectAsState()

                onTagEvent(TagEvent.ResetTagSort)

                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Black
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeneralLayout(
                        toDoState = toDoState,
                        tagState = tagState,
                        onToDoEvent = onToDoEvent,
                        onTagEvent = onTagEvent,
                        onCalendarEvent = onCalendarEvent,
                        calendarState = calendarState
                    )
                }
            }
        }
    }
}
