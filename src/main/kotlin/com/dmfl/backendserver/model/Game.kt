package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "GAMES")
data class Game(
    @Id var name: String,
    val homeScore: Int,
    val awayScore: Int,
    val time: String,
    val isPlayoff: Boolean
)
