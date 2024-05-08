package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYERS")
data class Player(
        @Id var pId: String?,
        val firstName: String?,
        val lastName: String?) {
    val number: Int = 0
    val positions: List<String> = mutableListOf()
    val captain: Boolean = false
}
