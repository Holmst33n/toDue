package com.example.todue.ui.screens.calendar

import android.widget.CalendarView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.elevatedButtonElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.todue.dataLayer.source.local.ToDo
import com.example.todue.state.CalendarState
import com.example.todue.state.TagState
import com.example.todue.state.ToDoState
import com.example.todue.ui.event.CalendarEvent
import com.example.todue.ui.event.TagEvent
import com.example.todue.ui.event.ToDoEvent
import com.example.todue.ui.screens.CheckToDoDialog
import com.example.todue.ui.screens.CreateToDoDialog
import com.example.todue.ui.screens.DeleteToDoDialog
import com.example.todue.ui.screens.EditToDoDialog
import com.example.todue.ui.screens.PlusButtonRow
import com.example.todue.ui.screens.ScrollableTagRow
import com.example.todue.ui.screens.ScrollableToDoColumn
import com.example.todue.ui.screens.TopBar
import com.example.todue.ui.theme.backgroundColor
import com.example.todue.ui.theme.buttonColor
import com.example.todue.ui.theme.itemColor
import com.example.todue.ui.theme.selectedItemColor
import com.example.todue.ui.theme.textColor
import com.example.todue.ui.theme.unselectedItemColor

@Composable
fun CalendarToDoList(
    toDoState: ToDoState,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    calendarState: CalendarState
) {
    var clickedFinished by remember { mutableStateOf(false) }

    var selectedToDo by remember {
        mutableStateOf(
            ToDo(
                title = "",
                description = "",
                tag = "",
                dueDate = "",
                dueTime = "",
                finished = false
            ) )
    }

    var selectedDate by remember { mutableStateOf(calendarState.givenDate) }
    var monthZero = ""
    var dayZero = ""

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        item{
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(factory = { CalendarView(it) } , update = {
                    it.setOnDateChangeListener { _, year, month, day ->
                        if (day < 10) {
                            dayZero = "0"
                        }
                        if (month < 10) {
                            monthZero = "0"
                        }
                        selectedDate = "$year-$monthZero${month + 1}-$dayZero$day"
                        onCalendarEvent(CalendarEvent.SortToDosByGivenDate(selectedDate))
                    }
                })
            }
        }

        items(calendarState.toDos) { toDo ->
            val (_, width) = LocalConfiguration.current.run { screenHeightDp.dp to screenWidthDp.dp }

            if (toDoState.isDeletingToDo) { DeleteToDoDialog(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, toDo = selectedToDo) }

            if(toDoState.isCheckingToDo) { CheckToDoDialog(onToDoEvent = onToDoEvent, toDo = selectedToDo) }

            if(toDoState.isEditingToDo) { EditToDoDialog(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, toDo = selectedToDo, toDoState = toDoState) }

            ElevatedButton(
                onClick = {
                    selectedToDo = toDo
                    onToDoEvent(ToDoEvent.ShowToDoDialog)
                },
                modifier = Modifier
                    .border(border = BorderStroke(10.dp, Color.Transparent))
                    .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                    .requiredWidth(width - 40.dp)
                    .fillMaxHeight(),
                elevation = elevatedButtonElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(itemColor),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .requiredHeight(100.dp)
                            .weight(0.7f)
                    ) {
                        Text(
                            text = toDo.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = toDo.description,
                            fontSize = 18.sp,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        var hashtag = ""
                        if(toDo.tag.isNotBlank()) {
                            hashtag = "#"
                        }
                        Text(
                            text = hashtag + toDo.tag,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = unselectedItemColor,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .size(10.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .requiredHeight(100.dp)
                    ) {
                        Text(
                            text = toDo.dueDate + "\n" + toDo.dueTime,
                            fontSize = 15.sp,
                            color = textColor,
                            textAlign = TextAlign.End
                        )
                        FloatingActionButton(
                            onClick = {
                                selectedToDo = toDo
                                clickedFinished = if(toDo.finished){
                                    onToDoEvent(ToDoEvent.UnFinishToDo(toDo = toDo))
                                    onTagEvent(TagEvent.CreateTag(title = toDo.tag))
                                    false
                                } else{
                                    onToDoEvent(ToDoEvent.FinishToDo(toDo = toDo))
                                    onTagEvent(TagEvent.DecreaseToDoAmount(title = toDo.tag))
                                    true
                                }
                            },
                            modifier = Modifier
                                .requiredSize(30.dp),
                            containerColor = buttonColor,
                            contentColor = backgroundColor,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            if(toDo.finished){
                                Icon(Icons.Filled.CheckBox, "Floating toggled filter button")
                            } else{
                                Icon(Icons.Filled.CheckBoxOutlineBlank, "Floating untoggled filter button")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(
    toDoState: ToDoState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    calendarState: CalendarState
) {
    ScaffoldCalendar(
        toDoState = toDoState,
        onTagEvent = onTagEvent,
        onToDoEvent = onToDoEvent,
        onCalendarEvent = onCalendarEvent,
        calendarState = calendarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldCalendar(
    toDoState: ToDoState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    calendarState: CalendarState
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                ),
                title = {
                    Text(
                        "Calendar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 30.sp
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),

            ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 5.dp, bottom = 5.dp)
                        ) {
                            if(toDoState.isCreatingToDo){
                                CreateToDoDialog(toDoState = toDoState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, onCalendarEvent = onCalendarEvent)
                                println(calendarState.givenDate)
                            }
                            CalendarToDoList(toDoState, onToDoEvent, onTagEvent, onCalendarEvent, calendarState)
                            PlusButtonRow(onToDoEvent)
                        }
                    }
                }
            }


        }
    }
}

