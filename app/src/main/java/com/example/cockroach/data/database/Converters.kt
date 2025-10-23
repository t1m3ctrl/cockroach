package com.example.cockroach.data.database

import androidx.room.TypeConverter
import com.example.cockroach.data.model.DifficultyLevel
import com.example.cockroach.data.model.Gender
import com.example.cockroach.data.model.ZodiacSign
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromGender(value: Gender?): String? {
        return value?.name
    }

    @TypeConverter
    fun toGender(value: String?): Gender? {
        return value?.let { Gender.valueOf(it) }
    }

    @TypeConverter
    fun fromZodiacSign(value: ZodiacSign?): String? {
        return value?.name
    }

    @TypeConverter
    fun toZodiacSign(value: String?): ZodiacSign? {
        return value?.let { ZodiacSign.valueOf(it) }
    }

    @TypeConverter
    fun fromDifficultyLevel(value: DifficultyLevel?): String? {
        return value?.name
    }

    @TypeConverter
    fun toDifficultyLevel(value: String?): DifficultyLevel? {
        return value?.let { DifficultyLevel.valueOf(it) }
    }
}