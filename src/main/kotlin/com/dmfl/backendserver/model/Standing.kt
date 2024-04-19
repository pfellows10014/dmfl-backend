package com.dmfl.backendserver.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@Entity
@Table(name = "STANDINGS")
data class Standing(
    @Id var teamName: String?,
    var wins: Double,
    var losses: Double,
    var winPercentage: Double,
    var pointsFor: Int,
    var pointsAgainst: Int,
    var pointDifferential: Int,
    var streak: Int)