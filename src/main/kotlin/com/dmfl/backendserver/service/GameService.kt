package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Game
import com.dmfl.backendserver.repository.GameRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class GameService(val db:GameRepository) {

    fun save(game: Game) {
        db.saveAndFlush(game)
    }

    fun findGameByName(name: String): Game? = db.findById(name).getOrNull()

    fun findGamesByTeam(teamName: String): List<Game> = db.findAll().filter { it.name.contains(teamName) }
}
