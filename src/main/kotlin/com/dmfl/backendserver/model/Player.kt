package com.dmfl.backendserver.model

import jakarta.persistence.*

@Entity
@Table(name = "PLAYERS")
data class Player(
    @Id var name: String?,
    val number: Long?,
    val positions: List<String>,
    val captain: Boolean)
