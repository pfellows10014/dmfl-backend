package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Roster
import com.dmfl.backendserver.repository.RosterRepository
import org.springframework.stereotype.Service

@Service
class RosterService(private val db: RosterRepository) {

    fun save(roster: Roster){
        db.saveAndFlush(roster)
    }
}