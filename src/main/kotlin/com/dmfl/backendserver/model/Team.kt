package com.dmfl.backendserver.model

import jakarta.persistence.*

@Entity
@Table(name = "TEAMS")
data class Team(
    @Id
    var name: String?,
    @OneToMany
    @JoinTable(name = "TEAM_PLAYER", joinColumns = [
        JoinColumn(name = "team_name", referencedColumnName = "name") ], inverseJoinColumns = [
        JoinColumn(name = "player_name", referencedColumnName = "name") ])
    var players: List<Player>)