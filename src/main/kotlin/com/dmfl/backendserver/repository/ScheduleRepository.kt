package com.dmfl.backendserver.repository

import com.dmfl.backendserver.model.Schedule
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository : CrudRepository<Schedule, String>