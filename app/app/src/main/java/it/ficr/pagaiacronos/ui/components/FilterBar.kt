package it.ficr.pagaiacronos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.ficr.pagaiacronos.R
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.local.dao.EventProjection
import it.ficr.pagaiacronos.data.repository.ResultsFilter
import it.ficr.pagaiacronos.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    filter: ResultsFilter,
    clubs: List<ClubProjection>,
    venues: List<EventProjection>,
    distances: List<Int>?,
    athleteSuggestions: List<String>,
    onFilterChange: (ResultsFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var clubMenuExpanded by remember { mutableStateOf(false) }
    var venueMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            // Toggle row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_title),
                    style = MaterialTheme.typography.titleSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (filter.activeCount > 0) {
                        TextButton(onClick = { onFilterChange(ResultsFilter()) }) {
                            Text(stringResource(R.string.filter_reset))
                        }
                    }
                    BadgedBox(badge = {
                        if (filter.activeCount > 0) Badge { Text(filter.activeCount.toString()) }
                    }) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.FilterListOff
                                              else Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter_title)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Athlete name
                    OutlinedTextField(
                        value = filter.athleteName,
                        onValueChange = { onFilterChange(filter.copy(athleteName = it)) },
                        label = { Text(stringResource(R.string.filter_athlete)) },
                        placeholder = { Text(stringResource(R.string.filter_athlete_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Boat class chips
                    ChipGroup(
                        label = stringResource(R.string.filter_boat_class),
                        options = Constants.BOAT_CLASSES,
                        selected = filter.boatClasses,
                        onToggle = { cls ->
                            val next = if (cls in filter.boatClasses)
                                filter.boatClasses - cls else filter.boatClasses + cls
                            onFilterChange(filter.copy(boatClasses = next))
                        },
                        labelOf = { it }
                    )

                    // Distance chips
                    ChipGroup(
                        label = stringResource(R.string.filter_distance),
                        options = distances!!,
                        selected = filter.distances,
                        onToggle = { d ->
                            val next = if (d in filter.distances)
                                filter.distances - d else filter.distances + d
                            onFilterChange(filter.copy(distances = next))
                        },
                        labelOf = { "${it}m" }
                    )

                    // Club dropdown
                    ExposedDropdownMenuBox(
                        expanded = clubMenuExpanded,
                        onExpandedChange = { clubMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = clubs.find { it.club_code == filter.clubCode }?.club
                                ?: stringResource(R.string.filter_club_all),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.filter_club)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(clubMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = clubMenuExpanded,
                            onDismissRequest = { clubMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.filter_club_all)) },
                                onClick = {
                                    onFilterChange(filter.copy(clubCode = null))
                                    clubMenuExpanded = false
                                }
                            )
                            clubs.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.club) },
                                    onClick = {
                                        onFilterChange(filter.copy(clubCode = c.club_code))
                                        clubMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Venue dropdown
                    ExposedDropdownMenuBox(
                        expanded = venueMenuExpanded,
                        onExpandedChange = { venueMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = filter.fickEventId ?: stringResource(R.string.filter_venue_all),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.filter_venue)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(venueMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = venueMenuExpanded,
                            onDismissRequest = { venueMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.filter_venue_all)) },
                                onClick = {
                                    onFilterChange(filter.copy(fickEventId = null))
                                    venueMenuExpanded = false
                                }
                            )
                            venues.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text(v.name) },
                                    onClick = {
                                        onFilterChange(filter.copy(fickEventId = v.fick_event_id))
                                        venueMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
