package com.dmfl.backendserver.model

import com.dmfl.backendserver.converters.RosterKeyConversions
import com.dmfl.backendserver.util.AppUtil
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Year

@Entity
@Table(name = "TEAMS")
data class Team(
        @Id val teamId: String = AppUtil.getUniqueId(5)) {
    lateinit var name: String
    var isActive: Boolean = false

    @OneToMany
    @JoinTable(name = "TEAM_ROSTER", joinColumns = [
        JoinColumn(name = "team_id", referencedColumnName = "teamId")], inverseJoinColumns = [
        JoinColumn(name = "roster_id", referencedColumnName = "rosterId")])
    @JsonSerialize(keyUsing = RosterKeyConversions.Serializer::class, contentAs = Roster::class)
    @JsonDeserialize(keyUsing = RosterKeyConversions.KeyDeserializer::class, contentAs = Roster::class)
//    @JsonDeserialize(using = RosterKeyConversions.Deserializer::class)
    var rosterMap: Map<Pair<String, Year>, Roster> = mutableMapOf()
//    lateinit var rosterMap: Map<String, Roster>

    @OneToMany
    @JoinTable(name = "TEAM_STATS", joinColumns = [
        JoinColumn(name = "team_id", referencedColumnName = "teamId")], inverseJoinColumns = [
        JoinColumn(name = "stats_id", referencedColumnName = "statsId")])
//    lateinit var teamMap: Map<Pair<String, Year>, Statistics>
    var teamStats: Map<String, Statistics> = mutableMapOf()
}