@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.settings.prayer

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.constants.Reciter
import de.yanos.islam.util.constants.School
import de.yanos.islam.util.helper.IslamDropDown
import de.yanos.islam.util.helper.IslamSwitch
import de.yanos.islam.util.helper.headlineMedium
import de.yanos.islam.util.helper.titleMedium


@Composable
@Preview
fun PrayerSettingView(
    modifier: Modifier = Modifier,
    vm: PrayerSettingViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_section_prayer), style = headlineMedium()) },
                navigationIcon = {
                    IconButton(onClick = { onNavigationChange(NavigationAction.NavigateBack) }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "BackNavigation")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                SourceSection(
                    method = vm.method,
                    school = vm.school,
                    reciter = vm.reciter,
                    onMethodChanged = vm::updateMethod,
                    onSchoolChanged = vm::updateSchool,
                    onReciterChanged = vm::updateReciter
                )
            }
            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.settings_prayer_times_label),
                        style = titleMedium()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.imsakReminder,
                    prayerReminderDelay = vm.imsakDelay,
                    text = R.string.settings_prayer_imsak_reminder,
                    index = 0,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.fajrReminder,
                    prayerReminderDelay = vm.fajrDelay,
                    text = R.string.settings_prayer_fajr_reminder,
                    index = 1,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.dhuhrReminder,
                    prayerReminderDelay = vm.dhuhrDelay,
                    text = R.string.settings_prayer_dhuhr_reminder,
                    index = 2,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.asrReminder,
                    prayerReminderDelay = vm.asrDelay,
                    text = R.string.settings_prayer_asr_reminder,
                    index = 3,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.maghribReminder,
                    prayerReminderDelay = vm.maghribDelay,
                    text = R.string.settings_prayer_maghrib_reminder,
                    index = 4,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
            item {
                PrayerTime(
                    isRemindEnabled = vm.ishaReminder,
                    prayerReminderDelay = vm.ishaDelay,
                    text = R.string.settings_prayer_isha_reminder,
                    index = 5,
                    onDelayChange = vm::imsakDelayChange,
                    onReminderChange = vm::imsakReminderChange
                )
            }
        }
    }
}

@Composable
fun SourceSection(
    modifier: Modifier = Modifier,
    method: Method,
    school: School,
    reciter: Reciter,
    onMethodChanged: (String) -> Unit,
    onSchoolChanged: (String) -> Unit,
    onReciterChanged: (String) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.settings_prayer_source_label),
            style = titleMedium()
        )
        Spacer(modifier = Modifier.height(8.dp))
        ElevatedCard(
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                School(school = school, onSchoolChanged = onSchoolChanged)
                Spacer(modifier = Modifier.height(12.dp))
                Method(method = method, onMethodChanged = onMethodChanged)
                Spacer(modifier = Modifier.height(12.dp))
                Reciter(reciter = reciter, onReciterChanged = onReciterChanged)
            }

        }
    }
}

@Composable
fun PrayerTime(
    modifier: Modifier = Modifier,
    isRemindEnabled: Boolean = false,
    prayerReminderDelay: Int = -5,
    @StringRes text: Int = R.string.settings_prayer_fajr_reminder,
    index: Int = 1,
    onDelayChange: (Int, index: Int) -> Unit = { _, _ -> },
    onReminderChange: (Boolean, index: Int) -> Unit = { _, _ -> }
) {
    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Column {
            IslamSwitch(modifier = Modifier.padding(8.dp), isChecked = isRemindEnabled, onCheckChange = { _ -> onReminderChange(!isRemindEnabled, index) }) {
                Text(modifier = Modifier, textAlign = TextAlign.Start, text = stringResource(id = text), style = titleMedium())
            }
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedVisibility(visible = isRemindEnabled, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                IslamDropDown(
                    modifier = modifier
                        .padding(8.dp)
                        .wrapContentWidth(),
                    selectedValue = prayerReminderDelay.toString(),
                    options = listOf(-45, -40, -35, -30, -25, -20, -15, -10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45).map { it.toString() },
                    label = stringResource(id = R.string.settings_prayer_delay_label),
                    onValueChangedEvent = { onDelayChange(it.toInt(), index) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@Composable
@Preview
private fun Method(
    modifier: Modifier = Modifier,
    method: Method = Method.Diyanet,
    onMethodChanged: (String) -> Unit = {}
) {
    IslamDropDown(
        modifier = modifier.padding(8.dp),
        selectedValue = stringResource(id = method.res),
        options = Method.entries.map { stringResource(id = it.res) },
        label = stringResource(id = R.string.settings_prayer_method_label),
        onValueChangedEvent = onMethodChanged
    )
}


@Composable
@Preview
private fun School(
    modifier: Modifier = Modifier,
    school: School = School.Hanafi,
    onSchoolChanged: (String) -> Unit = {}
) {
    IslamDropDown(
        modifier = modifier.padding(8.dp),
        selectedValue = stringResource(id = school.res),
        options = School.entries.map { stringResource(id = it.res) },
        label = stringResource(id = R.string.settings_prayer_school_label),
        onValueChangedEvent = onSchoolChanged
    )
}

@Composable
@Preview
private fun Reciter(
    modifier: Modifier = Modifier,
    reciter: Reciter = Reciter.Alafasi,
    onReciterChanged: (String) -> Unit = {}
) {
    IslamDropDown(
        modifier = modifier.padding(8.dp),
        selectedValue = stringResource(id = reciter.res),
        options = Reciter.entries.map { stringResource(id = it.res) },
        label = stringResource(id = R.string.settings_prayer_reciter_label),
        onValueChangedEvent = onReciterChanged
    )
}
