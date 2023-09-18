package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : CrudRepository<Player, String>