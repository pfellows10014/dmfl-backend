package com.dmfl.backendserver.model

import com.dmfl.backendserver.util.AppUtil
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Year

@Entity
@Table(name = "STATISTICS")
data class Statistics(
        @Id val statsId: String,
        val year: Year,
        val season: String) {
    var numberOfPassTds: Int = 0
    var numberOfRushTds: Int = 0
    var numberOfReceptionTds: Int = 0
    var numberOfThrownInterceptions:Int = 0
    var numberOfSacks: Int = 0
    var numberOfSafeties: Int = 0
    var numberOfDefenseInterceptions: Int = 0
    var numberOfDefenseTds: Int = 0
}
