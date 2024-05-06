package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Year

@Entity
@Table(name = "SCHEDULES")
data class Schedule(
        @Id
        val scheduleId: String,
        val week: String?,
        val year: Year,
        val season: String,
        val isPlayoffs: Boolean) {
    var current: Boolean = false

    @OneToMany
    @JoinTable(name = "SCHEDULE_GAMES", joinColumns = [
        JoinColumn(name = "schedule_id", referencedColumnName = "scheduleId")], inverseJoinColumns = [
        JoinColumn(name = "game_id", referencedColumnName = "gameId")])
    var games: List<Game> = mutableListOf()
}
