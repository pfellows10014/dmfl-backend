package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.apache.commons.lang3.StringUtils

@Entity
@Table(name = "STANDINGS")
data class Standing(
    @Id var teamName: String?,
    ) {
    var wins: Double = 0.0
    var losses: Double = 0.0
    var winPercentage: Double = 0.0
    var pointsFor: Int = 0
    var pointsAgainst: Int = 0
    var pointDifferential: Int = 0
    var streak: String = StringUtils.EMPTY
}