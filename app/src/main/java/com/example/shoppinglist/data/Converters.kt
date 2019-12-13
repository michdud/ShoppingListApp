package com.example.shoppinglist.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String): Category {
        return when(value) {
            "CLOTHING" -> Category.CLOTHING
            "ENTERTAINMENT" -> Category.ENTERTAINMENT
            "ELECTRONICS" -> Category.ELECTRONICS
            "FOOD" -> Category.FOOD
            "HOME" -> Category.HOME
            else -> Category.HOME
        }
    }

    @TypeConverter
    fun categoryToString(category: Category): String {
        return category.toString().toUpperCase()
    }
}