package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Game
import com.dmfl.backendserver.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, String> {

    @Query("SELECT g FROM Game g WHERE g.homeTeam = ?1 OR g.awayTeam = ?1")
    fun findByFirstAndLastName(teamName: String?): Game
}