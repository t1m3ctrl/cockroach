package com.example.cockroach.data.model

import com.example.cockroach.R

data class Author(
    val name: String,
    val photoResId: Int = R.drawable.ic_person_placeholder,
    val role: String = "Студент",
    val group: String = ""
)