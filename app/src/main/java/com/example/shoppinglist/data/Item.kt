package com.example.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")
data class Item (
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "category") var category : Category,
    @ColumnInfo(name = "name") var name : String,
    @ColumnInfo(name = "description") var description : String,
    @ColumnInfo(name = "price") var price : Double,
    @ColumnInfo(name = "status") var status : Boolean,
    @ColumnInfo(name = "quantity") var quantity : Int
) : Serializable