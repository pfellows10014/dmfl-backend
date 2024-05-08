package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Standing
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StandingRepository : JpaRepository<Standing, String>