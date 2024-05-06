package com.dmfl.backendserver.converters

import com.dmfl.backendserver.model.Roster
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import java.time.Year

private typealias RosterKey = Pair<String, Year>
private typealias RosterMap = Map<RosterKey, Roster>

object RosterKeyConversions {

    private val log = KotlinLogging.logger {}

    const val SEASON_FIELD_NAME = "season"
    const val YEAR_FIELD_NAME = "year"
    object Serializer: JsonSerializer<RosterKey>() {
        override fun serialize(value: RosterKey, gen: JsonGenerator, serializers: SerializerProvider?) {
            with(gen) {
               writeFieldName(value.first + "-" + value.second.toString())
            }
        }
    }

    object KeyDeserializer: com.fasterxml.jackson.databind.KeyDeserializer() {
        override fun deserializeKey(key: String, ctxt: DeserializationContext): RosterKey {
            log.info("In key deserializer: key: {}", key)
            val keyPair = StringUtils.split(key, "-")

            return RosterKey(keyPair[0], Year.of(keyPair[1].toInt()))
        }
    }

}