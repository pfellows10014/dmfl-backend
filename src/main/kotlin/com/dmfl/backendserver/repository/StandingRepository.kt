package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Standing
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StandingRepository : CrudRepository<Standing, String>