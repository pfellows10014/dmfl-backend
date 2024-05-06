package com.dmfl.backendserver.model

import com.dmfl.backendserver.util.AppUtil
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYERS")
data class Player(
        @Id val playerId: String = AppUtil.getUniqueId(5),
        val firstName: String?,
        val lastName: String?) {
    var number: Int = 0
    var captain: Boolean = false
    var positions: List<String> = mutableListOf()

    @OneToOne
    @JoinTable(name = "PLAYER_STATS", joinColumns = [
        JoinColumn(name = "player_id", referencedColumnName = "playerId")], inverseJoinColumns = [
        JoinColumn(name = "stats_id", referencedColumnName = "statsId")])
    var playerStats: Statistics? = null
}
