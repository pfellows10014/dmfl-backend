package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Statistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StatisticsRepository: JpaRepository<Statistics, String> {
}