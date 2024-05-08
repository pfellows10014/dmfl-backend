package com.dmfl.backendserver.util

import kotlin.random.Random

class AppUtil {

    companion object {
        fun getUniqueId(length: Int) = (0..9).shuffled().take(length).joinToString("")

        fun getRandom(length: Int) = generateSequence {
            Random.nextInt(1, 9)
        }.distinct().take(length).toString()
    }

}