package com.example.todue.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide
import com.example.todue.dataLayer.source.local.Tag
import com.example.todue.ui.event.TagEvent
import com.example.todue.dataLayer.source.local.ToDo
import com.example.todue.dataLayer.source.remote.ApiService
import com.example.todue.ui.event.ToDoEvent
import com.example.todue.ui.modifiers.getBottomLineShape
import com.example.todue.state.ToDoState
import com.example.todue.ui.theme.backgroundColor
import com.example.todue.ui.theme.buttonColor
import com.example.todue.ui.theme.selectedItemColor
import com.example.todue.ui.theme.textColor
import com.example.todue.ui.theme.unselectedItemColor
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToDoDialog(
    toDoState: ToDoState,
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    //for date and time pickers
    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var pickedTime by remember {
        mutableStateOf(LocalTime.now())
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var hourZero = ""
    var minuteZero = ""
    if(pickedTime.hour < 10){
        hourZero = "0"
    }
    if(pickedTime.minute < 10){
        minuteZero = "0"
    }

    val dueDateString: String = pickedDate.toString()
    onToDoEvent(ToDoEvent.SetDueDate(dueDateString))

    val dueTimeString: String = hourZero + pickedTime.hour.toString() + ":" + minuteZero + pickedTime.minute.toString()
    onToDoEvent(ToDoEvent.SetDueTime(dueTimeString))

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onToDoEvent(ToDoEvent.HideCreateDialog)
        },
        title = { Text(
            text = "Create To Do",
            color = MaterialTheme.colorScheme.onBackground
        ) },
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.title,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetTitle(it))
                    },
                    placeholder = {
                        Text(
                            text = "Title",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                )
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.description,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetDescription(it))
                    },
                    placeholder = {
                        Text(
                            text = "Description",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.tag,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetTag(it))
                    },
                    placeholder = {
                        Text(
                            text = "Tag",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )

                //date button
                Button(
                    onClick = {
                        dateDialogState.show()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Pick date")
                }
                Text(
                    text = dueDateString,
                    color = MaterialTheme.colorScheme.onBackground
                )

                //time button
                Button(
                    onClick = {
                        timeDialogState.show()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Pick time")
                }
                Text(
                    text = dueTimeString,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },

        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                //these variables are a quick fix for hour and minute not being saved correctly in database
                //previously, the first 0 was left out of hh and mm


                Button(
                    onClick = {
                        if(toDoState.title.isNotBlank()) {
                            onToDoEvent(ToDoEvent.CreateToDo)
                            onTagEvent(TagEvent.CreateTag(toDoState.tag))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(end = 5.dp)) {
                    Text(
                        text = "Create",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    )
    MaterialDialog(
        dialogState = dateDialogState,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        buttons = {
            positiveButton(
                text = "OK",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        },
    ) {
        this.datepicker(
            initialDate = LocalDate.now(),
            title = "Pick date",
            colors = DatePickerDefaults.colors(MaterialTheme.colorScheme.primary)
        ) {
            pickedDate = it
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        buttons = {
            positiveButton(
                text = "OK",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        }
    ) {
        this.timepicker(
            initialTime = LocalTime.now(),
            title = "Pick time",
            is24HourClock = true,
            colors = TimePickerDefaults.colors(MaterialTheme.colorScheme.primary)
        ) {
            pickedTime = it
        }
    }
}

@Composable
fun DeleteToDoDialog(
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    modifier: Modifier = Modifier,
    toDo: ToDo
) {

    AlertDialog(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        onDismissRequest = {
            onToDoEvent(ToDoEvent.HideDeleteToDoDialog)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Do you want to delete this ToDo?\nThis cannot be undone.",
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    onClick = {
                        onToDoEvent(ToDoEvent.DeleteToDo(toDo = toDo))
                        onTagEvent(TagEvent.DecreaseToDoAmount(toDo.tag))
                        onToDoEvent(ToDoEvent.HideDeleteToDoDialog)
                        onToDoEvent(ToDoEvent.HideToDoDialog)
                    },
                    modifier = Modifier
                        .padding(5.dp)
                    ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
            }
        }
    )

}

@Composable
fun CheckToDoDialog(
    onToDoEvent: (ToDoEvent) -> Unit,
    toDo: ToDo
) {

    AlertDialog(
        modifier = Modifier,
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        onDismissRequest = {
            onToDoEvent(ToDoEvent.HideToDoDialog)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = getBottomLineShape()
                        )
                        .padding(bottom = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = toDo.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Visible
                    )
                    FloatingActionButton(
                        onClick = {
                            onToDoEvent(ToDoEvent.SetToDoStateForEdit(toDo))
                            onToDoEvent(ToDoEvent.ShowEditToDoDialog)
                        },
                        modifier = Modifier
                            .requiredSize(30.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit, "Edit to-do button",
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                        )
                    }
                }
                Text(
                    text = toDo.description,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
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
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .padding(start = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        onClick = {
                            onToDoEvent(ToDoEvent.ShowDeleteToDoDialog)
                        }
                    ) {
                        Text(text = "Delete", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Box (
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = toDo.dueDate + "\n" + toDo.dueTime,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        onClick = {
                            onToDoEvent(ToDoEvent.HideToDoDialog)
                        }
                    ) {
                        Text(text = "OK", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    )

}

@Composable
fun DeleteTagDialog(
    onTagEvent: (TagEvent) -> Unit,
    onToDoEvent: (ToDoEvent) -> Unit,
    modifier: Modifier = Modifier,
    tag: Tag
) {

    AlertDialog(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        onDismissRequest = {
            onTagEvent(TagEvent.HideDeleteDialog)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Do you want to delete this tag?\nThis cannot be undone.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    onClick = {
                        onTagEvent(TagEvent.DeleteTag(title = tag.title))
                        onToDoEvent(ToDoEvent.DeleteTagFromToDos(tag = tag.title))
                        onTagEvent(TagEvent.HideDeleteDialog)
                    },
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
            }
        }
    )
}

@Composable
fun EditToDoDialog(
    toDo: ToDo,
    onToDoEvent: (ToDoEvent) -> Unit,
    onTagEvent: (TagEvent) -> Unit,
    toDoState: ToDoState
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    AlertDialog(
        onDismissRequest = {
            onToDoEvent(ToDoEvent.HideEditToDoDialog)
            onToDoEvent(ToDoEvent.ResetToDoState)
        },
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        title = { Text(
            text = "Edit To Do",
            color = MaterialTheme.colorScheme.onBackground
        ) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.title,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetTitle(it))
                    },
                    placeholder = {
                        Text(
                            text = "New title",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.description,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetDescription(it))
                    },
                    placeholder = {
                        Text(
                            text = "New description",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
                TextField(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    value = toDoState.tag,
                    onValueChange = {
                        onToDoEvent(ToDoEvent.SetTag(it))
                    },
                    placeholder = {
                        Text(
                            text = "New tag",
                            color = MaterialTheme.colorScheme.onBackground,
                            )
                    }
                )

                //date button
                Button(
                    onClick = {
                        dateDialogState.show()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Pick date",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                Text(
                    text = toDoState.dueDate,
                    color = MaterialTheme.colorScheme.onBackground,
                    )

                //time button
                Button(
                    onClick = {
                        timeDialogState.show()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                ) {
                    Text(text = "Pick time")
                }
                Text(
                    text = toDoState.dueTime,
                    color = MaterialTheme.colorScheme.onBackground
                )

            }
        },

        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {

                Button(
                    onClick = {
                        onToDoEvent(ToDoEvent.EditToDo(toDoState.title, toDoState.description, toDoState.tag, toDoState.dueDate, toDoState.dueTime, toDo.id))
                        onTagEvent(TagEvent.DecreaseToDoAmount(toDo.tag))
                        onTagEvent(TagEvent.CreateTag(toDoState.tag))
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(5.dp)) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    )
    MaterialDialog(
        dialogState = dateDialogState,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        buttons = {
            positiveButton(
                text = "OK",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        },
    ) {
        this.datepicker(
            initialDate = LocalDate.parse(toDoState.dueDate),
            title = "Pick date",
            colors = DatePickerDefaults.colors(MaterialTheme.colorScheme.primary)
        ) {
            onToDoEvent(ToDoEvent.SetDueDate(it.toString()))
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        backgroundColor = MaterialTheme.colorScheme.onTertiary,
        buttons = {
            positiveButton(
                text = "OK",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        }
    ) {
        this.timepicker(
            initialTime = LocalTime.parse(toDoState.dueTime),
            title = "Pick time",
            is24HourClock = true,
            colors = TimePickerDefaults.colors(MaterialTheme.colorScheme.primary)
        ) {
            onToDoEvent(ToDoEvent.SetDueTime(it.toString()))
        }
    }
}

@Composable
fun FinishToDoDialog(
    onToDoEvent: (ToDoEvent) -> Unit,
    ) {
    Dialog(onDismissRequest = { onToDoEvent(ToDoEvent.ResetToDoState) }) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(300.dp)
                .padding(16.dp),
            backgroundColor = MaterialTheme.colorScheme.onTertiary,
            shape = RoundedCornerShape(16.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "To-do Completed!",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                GifScreen()
            }
        }
    }
}

@Composable
fun GifScreen() {
    //MutableState is used to track Gif URLs
    //val context = LocalContext.current

    val searchTerm = "Applause @reactions"
    var imageUrl by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(searchTerm) {
        scope.launch(Dispatchers.IO) {
            imageUrl = try {
                val response = ApiService.RetrofitInstance.giphyApi.getRandomGif(
                    apiKey = ApiService.RetrofitInstance.API_KEY,
                    tag = searchTerm
                )
                response.data.images.downsized.url
            } catch (e: Exception) {
                "https://giphy.com/gifs/g5games-cat-g5-games-hidden-city-TNkxUEn97iluf7jCsh"
            }
        }
    }

    Column(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageUrl.isNotEmpty()) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        Glide.with(context)
                            .asGif()
                            .load(imageUrl)
                            .into(this)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
            )
        } else {
            Text(text = "Loading GIF...", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}