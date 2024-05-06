package com.dmfl.backendserver.service

import com.dmfl.backendserver.constants.AppConstants.Companion.DASH_SEPARATOR
import com.dmfl.backendserver.constants.AppConstants.Companion.POST_SEASON_ABBREVIATION
import com.dmfl.backendserver.constants.AppConstants.Companion.REGULAR_SEASON_ABBREVIATION
import com.dmfl.backendserver.constants.AppConstants.Companion.VERSUS_SEPARATOR
import com.dmfl.backendserver.model.Game
import com.dmfl.backendserver.repository.GameRepository
import com.dmfl.backendserver.util.AppUtil
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull
import kotlin.text.StringBuilder

@Service
class GameService(val db: GameRepository) {

    fun save(game: Game) {
        val newGame = Game(createGameId(game), game.year, game.season, game.week, game.homeTeam, game.awayTeam, game.isPlayoff)

        db.saveAndFlush(newGame)
    }

    fun findGameByName(name: String): Game? = db.findById(name).getOrNull()

    fun findGamesByTeam(teamName: String): List<Game> = db.findAll().filter { it.gameId.contains(teamName) }

    fun createGameId(game: Game): String {
        var gameId = StringBuilder()
                .append(game.homeTeam.name).append(DASH_SEPARATOR).append(VERSUS_SEPARATOR).append(DASH_SEPARATOR)
                .append(game.awayTeam.name).append(game.year).toString()

        gameId = if(game.isPlayoff)
            StringBuilder().append(gameId).append(POST_SEASON_ABBREVIATION).toString()
        else {
            StringBuilder().append(gameId).append(DASH_SEPARATOR).append(REGULAR_SEASON_ABBREVIATION).toString()
        }

        gameId = StringBuilder().append(gameId).append(DASH_SEPARATOR).append(game.week).toString()

        return gameId
    }
}
