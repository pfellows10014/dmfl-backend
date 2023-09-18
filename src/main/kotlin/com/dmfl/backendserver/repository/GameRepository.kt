package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Game
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : CrudRepository<Game, String>