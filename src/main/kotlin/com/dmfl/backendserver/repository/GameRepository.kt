package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, String>