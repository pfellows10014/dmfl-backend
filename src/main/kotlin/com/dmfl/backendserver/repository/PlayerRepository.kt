package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, String>