package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Standing
import com.dmfl.backendserver.repository.StandingRepository
import org.apache.commons.collections4.IteratorUtils
import org.springframework.stereotype.Service

@Service
class StandingService(val db:StandingRepository) {

    fun updateStandings(standing: Standing) {
        db.saveAndFlush(standing)
    }

    fun reset() {
        db.deleteAllInBatch()
    }

    fun findStandingByTeam(teamName: String): Standing = db.findById(teamName).orElse(Standing(teamName))

    fun findAllStandings(): List<Standing> = db.findAll().toList()

    private fun <T> Iterable<T>.toList(): List<T> =
        IteratorUtils.toList(this.iterator())
}