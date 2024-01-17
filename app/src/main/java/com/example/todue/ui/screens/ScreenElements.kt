package com.example.todue.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.elevatedButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todue.dataLayer.source.local.Tag
import com.example.todue.ui.event.TagEvent
import com.example.todue.dataLayer.source.local.ToDo
import com.example.todue.ui.event.ToDoEvent
import com.example.todue.navigation.TabItem
import com.example.todue.state.CalendarState
import com.example.todue.ui.modifiers.getBottomLineShape
import com.example.todue.state.TagState
import com.example.todue.state.ToDoState
import com.example.todue.ui.event.CalendarEvent
import com.example.todue.ui.screens.calendar.CalendarScreen
import com.example.todue.ui.screens.overview.ToDosScreen
import com.example.todue.ui.screens.settings.SettingsScreen
import com.example.todue.ui.screens.statistics.StatisticsScreen
import com.example.todue.ui.screens.tags.TagsScreen


//The general layout used on all the screens with navigation bar
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GeneralLayout(
    toDoState: ToDoState,
    tagState: TagState,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    calendarState: CalendarState
){


    val tabItems = listOf(
        TabItem(
            title = "Overview",
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Outlined.Home
        ),
        TabItem(
            title = "Tags",
            unselectedIcon = Icons.Outlined.Tag,
            selectedIcon = Icons.Outlined.Tag
        ),
        TabItem(
            title = "Calendar",
            unselectedIcon = Icons.Outlined.CalendarMonth,
            selectedIcon = Icons.Outlined.CalendarMonth
        ),
        TabItem(
            title = "Statistics",
            unselectedIcon = Icons.Outlined.Equalizer,
            selectedIcon = Icons.Outlined.Equalizer,
        ),
        TabItem(
            title = "Settings",
            unselectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Outlined.Settings
        )
    )
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    //checks if a scroll is in progress, used to avoid trouble when scrolling
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if(!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {index ->
            when(index){
                0 -> ToDosScreen(toDoState = toDoState, tagState = tagState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, onCalendarEvent = onCalendarEvent)
                1 -> TagsScreen(toDoState = toDoState, tagState = tagState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, onCalendarEvent = onCalendarEvent)
                2 -> CalendarScreen(onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, toDoState = toDoState, onCalendarEvent = onCalendarEvent, calendarState = calendarState)
                3 -> StatisticsScreen(toDoState = toDoState, onToDoEvent = onToDoEvent)
                4 -> SettingsScreen(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, onCalendarEvent = onCalendarEvent)
                else -> ToDosScreen(toDoState = toDoState, tagState = tagState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, onCalendarEvent = onCalendarEvent)
            }
        }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.tertiary,

            //indicator to avoid ugly line under selected tab
            indicator = {
                Color.Transparent
            }
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier,
                    unselectedContentColor = MaterialTheme.colorScheme.onSecondary,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    icon = {
                        Icon(
                            modifier = Modifier,
                            imageVector = if(index == selectedTabIndex){
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FilterButton(
    toDoState: ToDoState,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit
) {
    var clicked by remember { mutableStateOf(toDoState.sortByFinished) }

    FloatingActionButton(
        onClick = {
            clicked = if (!clicked) {
                onToDoEvent(ToDoEvent.SortToDosByFinished(true))
                onTagEvent(TagEvent.SetSortTagsByFinished(true))
                true
            } else {
                onToDoEvent(ToDoEvent.SortToDosByFinished(false))
                onTagEvent(TagEvent.SetSortTagsByFinished(false))
                false
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
            .requiredSize(50.dp)
    ) {
        when (clicked) {
            true -> Icon(Icons.Filled.CheckBox, "Floating toggled filter button")
            else -> Icon(Icons.Filled.CheckBoxOutlineBlank, "Floating untoggled filter button")
        }
    }
}


@Composable
fun TagList(
    tagState: TagState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    toDoState: ToDoState,
) {

    var selectedTag by remember {
        mutableStateOf(
            Tag(
                title = "",
                toDoAmount = 0,
                sort = false
            )
        )
    }

    val (_, width) = LocalConfiguration.current.run { screenHeightDp.dp to screenWidthDp.dp }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        TopBar(toDoState = toDoState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent)
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(tagState.tags) { tag ->

                if (tagState.isDeletingTag) {
                    DeleteTagDialog(
                        onTagEvent = onTagEvent,
                        onToDoEvent = onToDoEvent,
                        onCalendarEvent = onCalendarEvent,
                        tag = selectedTag
                    )
                }

                ElevatedButton(
                    onClick = {
                        selectedTag = tag
                        onTagEvent(TagEvent.ShowDeleteDialog)
                    },
                    modifier = Modifier
                        .border(border = BorderStroke(10.dp, Color.Transparent))
                        .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    elevation = elevatedButtonElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                    shape = RoundedCornerShape(10),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        horizontalArrangement =  Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .requiredWidth(width/6)
                                .height(30.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = tag.toDoAmount.toString() + " ToDos",
                                color = MaterialTheme.colorScheme.background,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                        Column(
                            modifier = Modifier
                                .width(width/3+30.dp)
                                .height(30.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = CenterHorizontally
                        ) {
                            Text(
                                text = "#" + tag.title,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.background,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            modifier = Modifier
                                .requiredWidth(width/6)
                                .height(30.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End
                        ) {
                            Icon(
                                Icons.Filled.Delete, "Delete tag",
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }
        }
    }
}

//List of to-do composables, used in the scrollable to-do column
@Composable
fun ToDoList(
    toDoState: ToDoState,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit
) {

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
    val (_, width) = LocalConfiguration.current.run { screenHeightDp.dp to screenWidthDp.dp }

    if(toDoState.isDeletingToDo) { DeleteToDoDialog(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, onCalendarEvent = onCalendarEvent, toDo = selectedToDo) }

    if(toDoState.isCheckingToDo) { CheckToDoDialog(onToDoEvent = onToDoEvent, toDo = selectedToDo) }

    if(toDoState.isEditingToDo) { EditToDoDialog(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, toDo = selectedToDo, toDoState = toDoState, onCalendarEvent = onCalendarEvent) }

    if(toDoState.isFinishingToDo) { FinishToDoDialog(onToDoEvent = onToDoEvent) }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        items(toDoState.toDos) { toDo ->

            ElevatedButton(
                onClick = {
                    selectedToDo = toDo
                    onToDoEvent(ToDoEvent.ShowToDoDialog)
                },
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                    .requiredWidth(width - 40.dp)
                    .fillMaxHeight(),
                elevation = elevatedButtonElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                ToDoItem(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, onCalendarEvent = onCalendarEvent, toDo = toDo)
            }
        }
        item{
           Spacer(
                modifier = Modifier
                    .height(80.dp)
            )
        }
    }
}

@Composable
fun ToDoItem(
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit,
    toDo: ToDo
){
    var clickedFinished by remember { mutableStateOf(false) }
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
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = toDo.description,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.primary,
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
                .requiredHeight(90.dp)
                .padding(top = 5.dp)
        ) {
            Text(
                text = toDo.dueDate + "\n" + toDo.dueTime,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
            FloatingActionButton(
                onClick = {
                    clickedFinished = if(toDo.finished){
                        onToDoEvent(ToDoEvent.UnFinishToDo(toDo = toDo))
                        onTagEvent(TagEvent.CreateTag(title = toDo.tag))
                        onCalendarEvent(CalendarEvent.Recompose)
                        false
                    } else{
                        onToDoEvent(ToDoEvent.FinishToDo(toDo = toDo))
                        onTagEvent(TagEvent.DecreaseToDoAmount(title = toDo.tag))
                        onCalendarEvent(CalendarEvent.Recompose)
                        true
                    }
                },
                modifier = Modifier
                    .requiredSize(30.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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

//Composable for the plus button at the bottom of the screen
//Is a row in a box to help with aligning it in the bottom right of the screen
@Composable
fun PlusButtonRow(
    onToDoEvent: (ToDoEvent) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.End,
        ) {
            FloatingActionButton(
                onClick = {
                    onToDoEvent(ToDoEvent.ShowCreateDialog)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(end = 20.dp, bottom = 10.dp)
                    .requiredSize(55.dp)
            ) {
                Icon(
                    Icons.Filled.Add, "Floating action button",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

//Scrollable column of ToDos
@Composable
fun ScrollableToDoColumn(
    toDoState: ToDoState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    onCalendarEvent: (CalendarEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        if(toDoState.isCreatingToDo){
            CreateToDoDialog(toDoState = toDoState, onTagEvent = onTagEvent, onToDoEvent = onToDoEvent, onCalendarEvent = onCalendarEvent)
        }
        ToDoList(toDoState = toDoState, onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, onCalendarEvent = onCalendarEvent)
        PlusButtonRow(onToDoEvent = onToDoEvent)
    }
}

//Row of scrollable tags
@Composable
fun ScrollableTagRow(
    tagState: TagState,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
) {

    LazyRow(
        modifier = Modifier
            .requiredHeight(50.dp)
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.onSecondary,
                shape = getBottomLineShape()
            )
            .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)

    ) {
        items(tagState.tags) { tag ->

            TagItem( onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, tag = tag)

        }
    }
}

@Composable
fun TagItem(
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    tag: Tag
){
    // Need to fix this. Doesn't use the light/dark themes, but instead hardcoded colors
    /*
    val buttonColor = remember { mutableStateOf(MaterialTheme.colorScheme.background) }

    if (!tag.sort) {
        buttonColor.value = MaterialTheme.colorScheme.background
    } else {
        buttonColor.value = MaterialTheme.colorScheme.primary
    }


     */
    OutlinedButton(
        onClick = {
            if(tag.sort) {
                onToDoEvent(ToDoEvent.RemoveTagToSortToDos(tag.title))
                onTagEvent(TagEvent.DontSortByThisTag(tag.title))
            } else {
                onToDoEvent(ToDoEvent.AddTagToSortToDos(tag.title))
                onTagEvent(TagEvent.SortByThisTag(tag))
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp),
        colors = ButtonDefaults.buttonColors(
            if (!tag.sort) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Text(
            text = "#" + tag.title,
            color = if(!tag.sort) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onTertiary
            }
        )
    }
}

@Composable
fun TopBar(
    toDoState: ToDoState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.09f)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.background,
                shape = getBottomLineShape()
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val focusManager = LocalFocusManager.current

        //BackHandler does not work yet for some reason
        BackHandler(enabled = true, onBack = {
            focusManager.clearFocus()
        })

        TextField(
            value = toDoState.searchInToDos,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                textColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                placeholderColor =  MaterialTheme.colorScheme.onSurface,
                cursorColor =  MaterialTheme.colorScheme.primary
            ),
            onValueChange = {
                onToDoEvent(ToDoEvent.SetSearchInToDos(it))
                onTagEvent(TagEvent.SetSearchInTags(it))
            },
            placeholder = {
                Text(
                    text = "search...",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            singleLine = true,
            modifier = Modifier
                .padding(end = 5.dp, start = 5.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onToDoEvent(ToDoEvent.SetSearchInToDos(""))
                        onTagEvent(TagEvent.SetSearchInTags(""))

                    }) {
                    Icon(Icons.Filled.ArrowBack, "Lose focus button",
                        tint = MaterialTheme.colorScheme.onBackground)

                }

            }
        )
        FilterButton(onToDoEvent = onToDoEvent, onTagEvent = onTagEvent, toDoState = toDoState)
    }
}

