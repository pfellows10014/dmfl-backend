package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : JpaRepository<Team, String>