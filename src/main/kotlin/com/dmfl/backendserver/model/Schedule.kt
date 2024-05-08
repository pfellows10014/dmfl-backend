package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "SCHEDULES")
data class Schedule(
    @Id var week: String?,
    val current: Boolean,
    val isPlayoffs: Boolean,
    @OneToMany
    val games: List<Game>)
