package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Team
import com.dmfl.backendserver.repository.TeamRepository
import mu.KotlinLogging
import org.apache.commons.collections4.IteratorUtils
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class TeamService(val db: TeamRepository, private val playerService: PlayerService) {
//    fun findAllTeams(): List<Team> = db.query("select * from messages") { response, _ ->
//        Team(response.getString("name"), response.getString("captain"),
//        Gson().fromJson(response.getString("roster"), Roster::class.java))
//    }

    private val log = KotlinLogging.logger {}

    fun findAllTeams(): List<Team> = db.findAll().toList()

    fun findTeamsByIsActive(isActive: Boolean): List<Team> = db.findByIsActive(isActive)

    fun findTeamByName(name: String): Team = db.findByName(name).get()

    fun findTeamById(teamId: String): Team? = db.findById(teamId).getOrNull()

    fun save(team: Team){
        log.debug("Saving and flushing: {}", team.name)
        try {
            db.saveAndFlush(team)
        } catch (e: Exception) {
            log.error("Error occurred while saving team: {}", e.stackTrace)
            throw Exception(e)
        }
    }

    fun delete(teamName: String): Unit = db.deleteById(teamName)

//    fun save(team: Team){
//        val roster = Gson().toJson(team.roster)
//        db.update("insert into teams values ( ?, ?, ? )",
//            team.name, team.captain, roster)
//    }

    private fun <T> Iterable<T>.toList(): List<T> =
        IteratorUtils.toList(this.iterator())

}
