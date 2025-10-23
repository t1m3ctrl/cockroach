package com.example.cockroach.ui.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cockroach.data.database.AppDatabase
import com.example.cockroach.data.model.GameRecordWithPlayer
import com.example.cockroach.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))

    private val _topRecords = MutableStateFlow<List<GameRecordWithPlayer>>(emptyList())
    val topRecords: StateFlow<List<GameRecordWithPlayer>> = _topRecords.asStateFlow()

    init {
        loadRecords(RecordFilter.TOP)
    }

    fun loadRecords(filter: RecordFilter) {
        viewModelScope.launch {
            when (filter) {
                RecordFilter.ALL -> {
                    repository.getTopRecordsWithPlayers(50).collect { records ->
                        _topRecords.value = records
                    }
                }
                RecordFilter.TOP -> {
                    repository.getTopRecordsWithPlayers(20).collect { records ->
                        _topRecords.value = records
                    }
                }
                RecordFilter.RECENT -> {
                    repository.getRecentRecordsWithPlayers(30).collect { records ->
                        _topRecords.value = records
                    }
                }
            }
        }
    }
}
