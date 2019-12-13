package com.example.shoppinglist.data

enum class Category {
    CLOTHING("Clothing"), ELECTRONICS("Electronics"), ENTERTAINMENT("Entertainment"), FOOD("Food"), HOME("Home");

    private var myName : String

    constructor(name: String) {
        this.myName = name
    }

    override fun toString(): String {
        return myName
    }
}