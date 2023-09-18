package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Team
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : CrudRepository<Team, String>