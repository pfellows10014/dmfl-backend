package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Player
import com.dmfl.backendserver.repository.PlayerRepository
import com.dmfl.backendserver.util.AppUtil
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class PlayerService(val db:PlayerRepository) {

    fun save(player: Player){
        player.pId = AppUtil.getUniquePlayerId(5)
        db.saveAndFlush(player)
    }

    fun findPlayerByFirstAndLastName(firstName: String, lastName: String):
            Player? = db.findByFirstAndLastName(firstName, lastName)

    fun findPlayerByName(name: String): Player? = db.findById(name).getOrNull()
}