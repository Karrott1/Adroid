package com.example.callreceive

object BlockedNumbersList {
    private val blockedNumbers = mutableListOf<String>()

    fun addNumber(number: String) {
        blockedNumbers.add(number)
    }

    fun isBlocked(number: String): Boolean {
        return blockedNumbers.contains(number)
    }
}
