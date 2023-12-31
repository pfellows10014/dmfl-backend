package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Player
import com.dmfl.backendserver.repository.PlayerRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class PlayerService(val db:PlayerRepository) {

    fun save(player: Player){
        db.save(player)
    }

    fun findPlayerByName(name: String): Player? = db.findById(name).getOrNull()
}