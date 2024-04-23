package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, String> {

    @Query("SELECT p FROM Player p WHERE p.firstName = ?1 AND p.lastName = ?2")
    fun findByFirstAndLastName(firstName: String?, lastName: String?): Player
}