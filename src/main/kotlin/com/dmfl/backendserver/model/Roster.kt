package com.dmfl.backendserver.model

import com.dmfl.backendserver.util.AppUtil
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "ROSTERS")
data class Roster(
        @Id val rosterId: String = AppUtil.getUniqueId(5)) {
    @ManyToMany
    @JoinTable(name = "ROSTER_PLAYER", joinColumns = [
        JoinColumn(name = "roster_id", referencedColumnName = "rosterId")], inverseJoinColumns = [
        JoinColumn(name = "player_id", referencedColumnName = "playerId")])
    var players: List<Player> = mutableListOf()
}
