package com.example.cockroach.domain.utils

import com.example.cockroach.data.model.ZodiacSign
import java.util.Calendar
import java.util.Date

object ZodiacCalc {
    fun calculateZodiacSign(birthDate: Date?): ZodiacSign? {
        if (birthDate == null) return null

        val calendar = Calendar.getInstance().apply {
            time = birthDate
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH начинается с 0

        return when (month) {
            1 -> if (day <= 19) ZodiacSign.CAPRICORN else ZodiacSign.AQUARIUS
            2 -> if (day <= 18) ZodiacSign.AQUARIUS else ZodiacSign.PISCES
            3 -> if (day <= 20) ZodiacSign.PISCES else ZodiacSign.ARIES
            4 -> if (day <= 19) ZodiacSign.ARIES else ZodiacSign.TAURUS
            5 -> if (day <= 20) ZodiacSign.TAURUS else ZodiacSign.GEMINI
            6 -> if (day <= 20) ZodiacSign.GEMINI else ZodiacSign.CANCER
            7 -> if (day <= 22) ZodiacSign.CANCER else ZodiacSign.LEO
            8 -> if (day <= 22) ZodiacSign.LEO else ZodiacSign.VIRGO
            9 -> if (day <= 22) ZodiacSign.VIRGO else ZodiacSign.LIBRA
            10 -> if (day <= 22) ZodiacSign.LIBRA else ZodiacSign.SCORPIO
            11 -> if (day <= 21) ZodiacSign.SCORPIO else ZodiacSign.SAGITTARIUS
            12 -> if (day <= 21) ZodiacSign.SAGITTARIUS else ZodiacSign.CAPRICORN
            else -> null
        }
    }
}