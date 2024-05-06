package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Roster
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RosterRepository: JpaRepository<Roster, String> {
}