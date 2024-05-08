package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "TEAMS")
data class Team(
    @Id
    var name: String?,
    @OneToMany
    @JoinTable(name = "TEAM_PLAYER", joinColumns = [
        JoinColumn(name = "team_name", referencedColumnName = "name") ], inverseJoinColumns = [
        JoinColumn(name = "player_id", referencedColumnName = "pId") ])
    var players: List<Player>)