package com.dmfl.backendserver.service

import com.dmfl.backendserver.model.Schedule
import com.dmfl.backendserver.repository.ScheduleRepository
import org.apache.commons.collections4.IteratorUtils
import org.springframework.stereotype.Service

@Service
class ScheduleService(val db:ScheduleRepository) {

    fun save(schedule: Schedule){
        db.saveAndFlush(schedule)
    }

    fun findAllSchedules(): List<Schedule> = db.findAll().toList()

    fun findScheduleByWeek(week: String): Schedule = db.findById(week).get()

    private fun <T> Iterable<T>.toList(): List<T> =
        IteratorUtils.toList(this.iterator())
}