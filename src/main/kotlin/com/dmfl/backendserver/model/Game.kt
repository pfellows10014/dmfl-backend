package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Year

@Entity
@Table(name = "GAMES")
data class Game(
        @Id
        val gameId: String,
        val year: Year,
        val season: String,
        val week: String,
        @OneToOne
        val homeTeam: Team,
        @OneToOne
        val awayTeam: Team,
        val isPlayoff: Boolean) {
    var homeScore: Int = 0
    var awayScore: Int = 0
    var time: String? = null
    @ManyToMany
    lateinit var homeTeamStats: Map<Player, Statistics>
    @ManyToMany
    lateinit var awayTeamStats: Map<Player, Statistics>
}
