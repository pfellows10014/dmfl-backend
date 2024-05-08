package com.dmfl.backendserver.model

import com.dmfl.backendserver.util.AppUtil
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYERS")
data class Player(
        @Id val pId: String = AppUtil.getUniqueId(5),
        val firstName: String?,
        val lastName: String?) {
    val number: Int = 0
    val positions: List<String> = mutableListOf()
    val captain: Boolean = false
}
