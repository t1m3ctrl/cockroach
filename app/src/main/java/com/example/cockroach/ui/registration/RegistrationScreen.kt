package com.example.cockroach.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cockroach.R
import com.example.cockroach.data.model.Gender
import com.example.cockroach.ui.theme.LightGreen80
import java.text.SimpleDateFormat
import java.util.*

/**
 * Главный экран регистрации игрока
 * @param modifier модификатор для настройки внешнего вида
 * @param viewModel ViewModel для управления состоянием
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = viewModel()
) {
    val playerData by viewModel.playerData
    val showResult by viewModel.showResult
//    val context = LocalContext.current

    // Состояние для выпадающего списка курсов
    var expandedCourses by remember { mutableStateOf(false) }

    // Состояние для календаря
    var showDatePicker by remember { mutableStateOf(false) }

    if (showResult) {
        // Показываем результат
        ResultScreen(
            playerData = playerData,
            onBack = { viewModel.hidePlayerData() },
            modifier = modifier
        )
    } else {
        // Показываем форму регистрации
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Text(
                text = stringResource(R.string.registration_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            // ФИО
            OutlinedTextField(
                value = playerData.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                label = { Text(stringResource(R.string.full_name_label)) },
                placeholder = { Text(stringResource(R.string.full_name_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = playerData.fullName.isBlank()
            )

            // Пол (RadioButton)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .selectableGroup()
                ) {
                    Text(
                        text = stringResource(R.string.gender_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Мужской
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = playerData.gender == Gender.MALE,
                                    onClick = { viewModel.updateGender(Gender.MALE) },
                                    role = Role.RadioButton
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = playerData.gender == Gender.MALE,
                                onClick = null
                            )
                            Text(
                                text = stringResource(R.string.gender_male),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Женский
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = playerData.gender == Gender.FEMALE,
                                    onClick = { viewModel.updateGender(Gender.FEMALE) },
                                    role = Role.RadioButton
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = playerData.gender == Gender.FEMALE,
                                onClick = null
                            )
                            Text(
                                text = stringResource(R.string.gender_female),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Курс (выпадающий список)
            ExposedDropdownMenuBox(
                expanded = expandedCourses,
                onExpandedChange = { expandedCourses = !expandedCourses }
            ) {
                OutlinedTextField(
                    value = "${playerData.course} курс",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.course_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCourses)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCourses,
                    onDismissRequest = { expandedCourses = false },
                    modifier = Modifier.background(LightGreen80)
                ) {
                    (1..6).forEach { course ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "$course курс",
                                    style = MaterialTheme.typography.bodyLarge

                                )
                            },
                            onClick = {
                                viewModel.updateCourse(course - 1)
                                expandedCourses = false
                            }
                        )
                    }
                }
            }

            // Уровень сложности (SeekBar/Slider)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.difficulty_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = viewModel.getDifficultyText(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Slider(
                        value = when(playerData.difficultyLevel.value) {
                            1 -> 0f
                            2 -> 1f
                            3 -> 2f
                            4 -> 3f
                            else -> 0f
                        },
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp) // размер "шарика"
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        },
                        onValueChange = { viewModel.updateDifficultyLevel(it) },
                        valueRange = 0f..3f,
                        steps = 2, // 4 значения: 0, 1, 2, 3
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Подписи к слайдеру
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Легкий",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Средний",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Сложный",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Эксперт",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Дата рождения
            OutlinedTextField(
                value = playerData.birthDate?.let {
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
                } ?: "",
                onValueChange = { },
                label = { Text(stringResource(R.string.birth_date_label)) },
                placeholder = { Text("Выберите дату рождения") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Text(
                            text = "Выбрать",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                isError = playerData.birthDate == null
            )

            // Знак зодиака
            if (playerData.zodiacSign != null) {
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.zodiac_sign_label),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Иконка знака зодиака
                            Text(
                                text = getZodiacIcon(playerData.zodiacSign!!),
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier.padding(8.dp)
                            )

                            Text(
                                text = playerData.zodiacSign!!.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка очистки
                OutlinedButton(
                    onClick = { viewModel.clearData() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        ).brush
                    )
                ) {
                    Text(
                        text = stringResource(R.string.clear_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Кнопка регистрации
                Button(
                    onClick = { viewModel.showPlayerData() },
                    modifier = Modifier.weight(1f),
                    enabled = viewModel.canSubmit(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.submit_button),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Дополнительный отступ внизу
            Spacer(modifier = Modifier.height(16.dp))
        }

        // DatePickerDialog
        if (showDatePicker) {
            DatePickerDialog(
                onDateSelected = { date ->
                    viewModel.updateBirthDate(date)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

/**
 * Экран результата регистрации
 */
@Composable
fun ResultScreen(
    playerData: com.example.cockroach.data.model.Player,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.result_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        // Карточка с данными игрока
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Информация об игроке",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                PlayerInfoRow(
                    label = "ФИО:",
                    value = playerData.fullName
                )

                PlayerInfoRow(
                    label = "Пол:",
                    value = playerData.gender?.displayName ?: "Не указан"
                )

                PlayerInfoRow(
                    label = "Курс:",
                    value = "${playerData.course} курс"
                )

                PlayerInfoRow(
                    label = "Сложность:",
                    value = playerData.difficultyLevel.displayName
                )

                PlayerInfoRow(
                    label = "Дата рождения:",
                    value = playerData.birthDate?.let {
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
                    } ?: "Не указана"
                )

                if (playerData.zodiacSign != null) {
                    PlayerInfoRow(
                        label = "Знак зодиака:",
                        value = playerData.zodiacSign.displayName
                    )
                }
            }
        }

        // Карточка со знаком зодиака
        if (playerData.zodiacSign != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                )
                {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ваш знак зодиака",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = getZodiacIcon(playerData.zodiacSign),
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text(
                            text = playerData.zodiacSign.displayName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка возврата
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = stringResource(R.string.back_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Дополнительный отступ внизу
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Компонент для отображения строки информации об игроке
 */
@Composable
private fun PlayerInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

/**
 * Диалог выбора даты
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        // Устанавливаем начальную дату на текущую дату минус 20 лет
        initialSelectedDateMillis = Calendar.getInstance().apply {
            add(Calendar.YEAR, -20)
        }.timeInMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Отмена",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true
        )
    }
}

/**
 * Возвращает эмодзи для знака зодиака
 */
private fun getZodiacIcon(zodiacSign: com.example.cockroach.data.model.ZodiacSign): String {
    return when (zodiacSign) {
        com.example.cockroach.data.model.ZodiacSign.ARIES -> "♈"
        com.example.cockroach.data.model.ZodiacSign.TAURUS -> "♉"
        com.example.cockroach.data.model.ZodiacSign.GEMINI -> "♊"
        com.example.cockroach.data.model.ZodiacSign.CANCER -> "♋"
        com.example.cockroach.data.model.ZodiacSign.LEO -> "♌"
        com.example.cockroach.data.model.ZodiacSign.VIRGO -> "♍"
        com.example.cockroach.data.model.ZodiacSign.LIBRA -> "♎"
        com.example.cockroach.data.model.ZodiacSign.SCORPIO -> "♏"
        com.example.cockroach.data.model.ZodiacSign.SAGITTARIUS -> "♐"
        com.example.cockroach.data.model.ZodiacSign.CAPRICORN -> "♑"
        com.example.cockroach.data.model.ZodiacSign.AQUARIUS -> "♒"
        com.example.cockroach.data.model.ZodiacSign.PISCES -> "♓"
    }
}