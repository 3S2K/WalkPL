package org.example.project.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun TrackGroup(
    tracks: List<Track>,
    viewModel: PlayerViewModel,
    showCategoryButtons: Boolean = false,
    categories: List<String> = emptyList(),
    selectedCategory: String = "",
    onCategorySelected: (String) -> Unit = {},
    showDateFilter: Boolean = false,
    availableDates: List<String> = emptyList(),
    selectedDate: String = "",
    onDateSelected: (String) -> Unit = {},
    showPlayAll: Boolean = false,
    emptyMessage: String,
    onTrackClick: (Track) -> Unit,
    isError: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCategoryButtons) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    categories.forEach { category ->
                        CategoryButton(
                            text = category,
                            isSelected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            enabled = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            if (showDateFilter) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val dateButtons = listOf(
                        "오늘" to availableDates.getOrNull(0),
                        "어제" to availableDates.getOrNull(1),
                        "2일 전" to availableDates.getOrNull(2)
                    )

                    dateButtons.forEach { (text, date) ->
                        DateFilterButton(
                            text = text,
                            isSelected = selectedDate == date,
                            onClick = { date?.let { onDateSelected(it) } },
                            enabled = date != null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            if (showPlayAll) {
                OutlinedButton(
                    onClick = { viewModel.playTracks(tracks) },
                    enabled = tracks.isNotEmpty(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.38f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.38f)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .padding(end = 4.dp)
                ) {
                    Text(
                        "모두 재생",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        if (tracks.isEmpty() || isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn {
                items(tracks) { track ->
                    TrackItem(
                        track = track,
                        onTrackClick = { onTrackClick(track) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DateFilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White.copy(alpha = 0.12f),
            disabledContentColor = Color.White.copy(alpha = 0.38f)
        )
    } else {
        ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.38f)
        )
    }

    if (isSelected) {
        Button(
            onClick = onClick,
            colors = buttonColors,
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier
                .height(32.dp)
                .padding(end = 4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            colors = buttonColors,
            enabled = enabled,
            border = BorderStroke(
                width = 1.dp,
                color = if (enabled) Color.White.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.38f)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier
                .height(32.dp)
                .padding(end = 4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    } else {
        ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.38f)
        )
    }

    if (isSelected) {
        Button(
            onClick = onClick,
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier
                .height(32.dp)
                .padding(end = 4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier
                .height(32.dp)
                .padding(end = 4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}