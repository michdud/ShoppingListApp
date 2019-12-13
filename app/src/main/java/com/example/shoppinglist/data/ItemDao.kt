package com.example.shoppinglist.data

import androidx.room.*

@Dao
interface ItemDao {
    @Query("SELECT * from item")
    fun getAllItems() : List<Item>

    @Query("SELECT * from item WHERE category = :category")
    fun getItemsFromCategory(category: Category): List<Item>

    @Insert
    fun insertItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("DELETE FROM item")
    fun deleteAllItems()

    @Query("SELECT SUM(price*quantity) from item WHERE category = :category")
    fun getTotalPriceOfCategory(category: Category): Double

    @Query("SELECT SUM(price*quantity) from item")
    fun getTotalPrice(): Double
}