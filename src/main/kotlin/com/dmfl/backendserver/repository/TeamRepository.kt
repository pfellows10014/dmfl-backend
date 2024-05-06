package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TeamRepository : JpaRepository<Team, String> {

    @Query("SELECT t FROM Team t WHERE t.isActive = ?1")
    fun findByIsActive(isActive: Boolean): List<Team>

    @Query("SELECT t FROM Team t WHERE t.name = ?1")
    fun findByName(teamName: String): Optional<Team>
}