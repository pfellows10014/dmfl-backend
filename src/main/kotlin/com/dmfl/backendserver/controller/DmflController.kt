package com.dmfl.backendserver.controller

import com.dmfl.backendserver.constants.AppConstants.Companion.MAX_POINT_DIFFERENTIAL
import com.dmfl.backendserver.model.Game
import com.dmfl.backendserver.model.Player
import com.dmfl.backendserver.model.Roster
import com.dmfl.backendserver.model.Schedule
import com.dmfl.backendserver.model.Standing
import com.dmfl.backendserver.model.Team
import com.dmfl.backendserver.service.GameService
import com.dmfl.backendserver.service.PlayerService
import com.dmfl.backendserver.service.RosterService
import com.dmfl.backendserver.service.ScheduleService
import com.dmfl.backendserver.service.StandingService
import com.dmfl.backendserver.service.TeamService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year
import java.util.Comparator.comparing
import kotlin.math.abs

@RestController
@RequestMapping("/api")
class DmflController(private val teamService: TeamService, private val playerService: PlayerService,
                     private val scheduleService: ScheduleService, private val gameService: GameService,
                     private val standingService: StandingService, private val rosterService: RosterService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/teams/all")
    fun getTeams(): List<Team> {

        return teamService.findAllTeams()
    }

    @GetMapping("teams/current")
    fun getCurrentTeam(): List<Team> {
        val isActive = true

        return teamService.findTeamsByIsActive(isActive)
    }

    @PostMapping("/teams/save")
    fun addTeam(@RequestBody team: Team, @RequestParam season: String, @RequestParam year: Year) {
        log.debug("Adding team: {}", team.name)

//        val teamRoster = team.rosterList.stream().filter { it.year == Year.of(2024) && it.season == "spr"}.findFirst().get()
        val keyPair = Pair(season, year)


        val teamRoster = team.rosterMap[keyPair]
//        val teamRoster = team.rosterMap["spr-2024"]

        if (teamRoster != null) {
            for(player in teamRoster.players){
                if(player.firstName != null && player.lastName != null){
                    try {
                        playerService.findPlayerByFirstAndLastName(player.firstName, player.lastName)
                    } catch (e: NoSuchElementException) {
                        log.warn("Player does not exist, creating new player")
                        playerService.save(player)
                    } catch (e: Exception) {
                        log.error("Error occurred while saving team: {}", e.stackTrace)
                        throw Exception(e)
                    }
                }
            }
            rosterService.save(teamRoster)
        }
        log.debug("Calling team service to save team: {}", team.name)
        teamService.save(team)
    }

    @PostMapping("/teams/{teamName}/remove")
    fun removeTeam(@PathVariable teamName: String) {
        log.debug("Removing team: {}", teamName)

        teamService.delete(teamName)
    }

    @PostMapping("/teams/{teamName}/players/add")
    fun addPlayerToTeam(@PathVariable teamName: String, @RequestParam firstName: String, @RequestParam lastName: String,
                        @RequestParam season: String, @RequestParam year: Year): ResponseEntity<String> {
        val keyPair = Pair(season, year)

        try {
            val team = teamService.findTeamByName(teamName)
            val player = playerService.findPlayerByFirstAndLastName(firstName, lastName)
            if(player != null) {
                log.info("Adding player {}, {} to Team {}", firstName, lastName, team.name)
//                val teamRoster = team.rosterList.stream().filter { it.year == Year.of(2024) && it.season == "spr"}.findFirst().get()
                val teamRoster = team.rosterMap[keyPair]
//                val teamRoster = team.rosterMap["spr-2024"]
                val arrayList = ArrayList(teamRoster!!.players)
//                val arrayList = ArrayList(team.roster.players)
                arrayList.add(player)
                teamRoster.players = arrayList;
//                team.roster.players = arrayList
                teamService.save(team)
                return ResponseEntity(HttpStatus.OK)
            }
            log.warn("Player {}, {} can not be added to non-existent team {}", firstName, lastName, teamName)
            return ResponseEntity("Team doesn't exist.", HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            log.error("Error occurred while adding player to team: {}", e.stackTrace)
            throw Exception(e)
        }
    }

    @PostMapping("/teams/{teamName}/players/remove")
    fun removePlayerFromTeam(@PathVariable teamName: String, @RequestParam firstName: String, @RequestParam lastName: String,
                             @RequestParam season: String, @RequestParam year: Year): ResponseEntity<String> {
        val keyPair = Pair(season, year)

        try {
            val team = teamService.findTeamByName(teamName)
//            val teamRoster = team?.rosterList?.stream()?.filter { it.year == Year.of(2024) && it.season == "spr"}?.findFirst()?.get()
            val teamRoster = team.rosterMap[keyPair]
//            val teamRoster = team!!.rosterMap["spr-2024"]

            if(teamRoster != null && teamRoster.players.isNotEmpty()) {
                val playersList = ArrayList(teamRoster.players)
                val playerIterator = playersList.iterator()
                while (playerIterator.hasNext()) {
                    val currentPlayer = playerIterator.next()
                    val requestedPlayer = playerService.findPlayerByFirstAndLastName(firstName, lastName)!!
                    if (currentPlayer.playerId == requestedPlayer.playerId)
                        log.info("Player {}, {} has being removed from {}", firstName, lastName, team.name)
                        playerIterator.remove()
                }
//                team.roster.players = playersList
//                teamService.save(team)
                teamRoster.players = playersList
                rosterService.save(teamRoster)
               //teamService.save(team)
                return ResponseEntity(HttpStatus.OK)
            }
            log.warn("Team {} does not exist or has no players to be removed", teamName)
            return ResponseEntity("Team doesn't exist or has no players.", HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            log.error("Error occurred while removing player from team: {}", e.stackTrace)
            throw Exception(e)
        }
    }

//    @GetMapping("/teams/{name}")
//    fun findTeam(@PathVariable name: String): Team? {
//        log.info("Team name: {}", name)
//
//
//        return teamService.findTeamByName(name)
//    }

    @GetMapping("/teams/{teamId}")
    fun findTeamById(@PathVariable teamId: String): Team? {
        log.info("Team teamId: {}", teamId)


        return teamService.findTeamById(teamId)
    }

    @GetMapping("/player/{name}")
    fun findPlayer(@RequestParam name: String) {
        playerService.findPlayerByName(name)
    }

    @PostMapping("/player/save")
    fun addPlayer(@RequestBody player: Player) {
        playerService.save(player)
    }

    @GetMapping("/schedule")
    fun getSchedule(): List<Schedule> {

        return scheduleService
            .findAllSchedules()
    }

    @GetMapping("/schedule/{week}")
    fun getSchedule(@PathVariable week: String): Schedule {

        return scheduleService.findScheduleByWeek(week)
    }

    @PostMapping("/schedule/save")
    fun addSchedule(@RequestBody schedule: Schedule){
        for(game in schedule.games){
            try {
                gameService.findGameByName(game.gameId)
            } catch (e: NoSuchElementException) {
                log.warn("Game does not exist, creating new game")
                gameService.save(game)
            } catch (e: Exception) {
                log.error("Error occurred while saving game: {}", e.stackTrace)
                throw Exception(e)
            }
        }
        scheduleService.save(schedule)
    }

    @PostMapping("/schedule/{week}/reset")
    fun setSchedule(@PathVariable week: String){
        log.debug("Resetting the schedule for {}", week)

        scheduleService.resetByWeek(week)

        log.debug("Schedule reset for: {}", week)
    }

    @GetMapping("/game/{name}")
    fun findGame(@PathVariable name: String) {
        gameService.findGameByName(name)
    }

    @PostMapping("/game/save")
    fun addGame(@RequestBody game: Game) {
        gameService.save(game)
    }

    @PostMapping("/game/update")
    fun updateScore(@RequestParam week: String, @RequestBody game: Game): ResponseEntity<String> {
        var gameExist = false

//        try{
            val schedule = scheduleService.findScheduleByWeek(week)
//            val currentGame = gameService.findGameByName(game.name)

            schedule.games.forEach {
                if(it.gameId == game.gameId) {
                    gameExist = true
                }
            }

            if(!schedule.current || !gameExist) {
                log.warn("Not current week or game doesn't exist")
            }

            if(game.time == "Final" && gameExist && !game.isPlayoff) {
                log.info("{} vs {}", game.homeTeam, game.awayTeam)

                val homeTeamStanding = standingService.findStandingByTeam(game.homeTeam.name)
                val awayTeamStanding = standingService.findStandingByTeam(game.awayTeam.name)
                var homeWins = homeTeamStanding.wins
                var homeLosses = homeTeamStanding.losses
                var homeStreak = homeTeamStanding.streak
                var homePointDif = homeTeamStanding.pointDifferential
                var awayWins = awayTeamStanding.wins
                var awayLosses = awayTeamStanding.losses
                var awayStreak = awayTeamStanding.streak
                var awayPointDif = awayTeamStanding.pointDifferential
                val homeScoreMinusAwayScore = game.homeScore - game.awayScore
                if(homeScoreMinusAwayScore > 0) {
                    ++homeWins
                    ++awayLosses
                    homeStreak = if(homeStreak == "0" || homeStreak.contains("W", ignoreCase = true)) {
                        var intStreak = homeStreak.substring(0, homeStreak.length - 1).toInt()
                        StringBuilder().append((++intStreak).toString()).append("W").toString()
                    } else {
                        "1W"
                    }
                    awayStreak = if(awayStreak == "0" || awayStreak.contains("L", ignoreCase = true)) {
                        var intStreak = awayStreak.substring(0, awayStreak.length - 1).toInt()
                        StringBuilder().append((++intStreak).toString()).append("L").toString()
                    } else {
                        "1L"
                    }
                    homePointDif += if (homeScoreMinusAwayScore > MAX_POINT_DIFFERENTIAL) MAX_POINT_DIFFERENTIAL else homeScoreMinusAwayScore
                    awayPointDif -= if (homeScoreMinusAwayScore > MAX_POINT_DIFFERENTIAL) MAX_POINT_DIFFERENTIAL else homeScoreMinusAwayScore
                } else {
                    ++homeLosses
                    ++awayWins
                    awayStreak = if(awayStreak == "0" || awayStreak.contains("W", ignoreCase = true)) {
                        var intStreak = awayStreak.substring(0, awayStreak.length - 1).toInt()
                        StringBuilder().append((++intStreak).toString()).append("W").toString()
                    } else {
                        "1W"
                    }
                    homeStreak = if(homeStreak == "0" || homeStreak.contains("L", ignoreCase = true)) {
                        var intStreak = homeStreak.substring(0, homeStreak.length - 1).toInt()
                        StringBuilder().append((++intStreak).toString()).append("L").toString()
                    } else {
                        "1L"
                    }
                    homePointDif -= if (abs(homeScoreMinusAwayScore) > MAX_POINT_DIFFERENTIAL) MAX_POINT_DIFFERENTIAL else abs(homeScoreMinusAwayScore)
                    awayPointDif += if (abs(homeScoreMinusAwayScore) > MAX_POINT_DIFFERENTIAL) MAX_POINT_DIFFERENTIAL else abs(homeScoreMinusAwayScore)
                }

                homeTeamStanding.wins = homeWins
                homeTeamStanding.losses = homeLosses
                homeTeamStanding.streak = homeStreak
                homeTeamStanding.winPercentage = (homeWins / (homeWins + homeLosses))
                homeTeamStanding.pointDifferential = homePointDif
                homeTeamStanding.pointsFor += game.homeScore
                homeTeamStanding.pointsAgainst += game.awayScore
                awayTeamStanding.wins = awayWins
                awayTeamStanding.losses = awayLosses
                awayTeamStanding.streak = awayStreak
                awayTeamStanding.winPercentage = awayWins / (awayWins + awayLosses)
                awayTeamStanding.pointDifferential = awayPointDif
                awayTeamStanding.pointsFor += game.awayScore
                awayTeamStanding.pointsAgainst += game.homeScore
                standingService.updateStandings(homeTeamStanding)
                standingService.updateStandings(awayTeamStanding)

            }
            gameService.save(game)
            return ResponseEntity(HttpStatus.OK)
//        } catch(e: Exception){
//            log.error("Error occurred: {}; {}", e.message, e.localizedMessage)
//            return ResponseEntity("No Score Updated", HttpStatus.BAD_REQUEST)
//        }
    }

    @PostMapping("/standing/update")
    fun updateStanding(@RequestBody standing: Standing) {
        standingService.updateStandings(standing)
    }

    @GetMapping("/standings")
    fun getStandings(): List<Standing> {
        val standingsGrouped = standingService.findAllStandings()
            .groupBy{ it.winPercentage }.toSortedMap(compareByDescending { it })

        log.info { standingsGrouped }


        val standingsOrder: ArrayList<Standing> = arrayListOf()
        standingsGrouped.forEach { group ->
            var currentGroupList = group.value
            if(currentGroupList.size > 1) {
                if(currentGroupList.size == 2) {
                    currentGroupList = twoTeamTiebreaker(group.value)
                    currentGroupList.forEach { standing ->
                        standingsOrder.add(standing)
                    }
                } else {
                    currentGroupList = threeOrMoreTeamTiebreaker(group.value)
                    currentGroupList.forEach { standing ->
                        standingsOrder.add(standing)
                    }
                }
            } else {
                standingsOrder.add(currentGroupList.first())
            }
        }

        return standingsOrder
    }

    @PostMapping("/standings/reset")
    fun resetStandings() {
        log.debug { "Resetting Standings" }

        standingService.reset()

        log.debug { "Standings reset" }
    }

    fun twoTeamTiebreaker(group: List<Standing>): List<Standing> {

        return group.sortedWith(
            comparing<Standing?, String?>(
                { it.teamName },
                { t1, t2 -> (headToHead(t1, t2)) }
            ).thenComparing(
                { it.pointDifferential },
                { pd1, pd2 -> (pd1.compareTo(pd2))},
            )
        ).reversed()
    }

    fun threeOrMoreTeamTiebreaker(group: List<Standing>): List<Standing> {

//        for(standing in group) {
//
//        }

//        return group.sortedWith(
//            comparing<Standing?, String?>(
//                { it.teamName },
//                { t1, t2 -> (strengthOfVictory(t1, t2))}
//            ).thenComparing(
//                { it.pointDifferential },
//                { pd1, pd2 -> (pd1.compareTo(pd2))},
//            )
//        ).reversed()

        return group.sortedWith(
                comparing<Standing?, String?>(
                        { it.teamName },
                        { t1, t2 -> (headToHead(t1, t2)) }
                ).thenComparing(
                        { it.pointDifferential },
                        { pd1, pd2 -> (pd1.compareTo(pd2))})
                .thenComparing<String?>(
                        { it.teamName },
                        { t1, t2 -> (strengthOfVictory(t1, t2))},
                )
        ).reversed()

    }

    fun strengthOfVictory(teamName: String, teamName2: String): Int {
        val teamOneOpponentList = gameService.findGamesByTeam(teamName)
        val teamTwoOpponentList = gameService.findGamesByTeam(teamName2)
        var teamOneOpponentWinPert = 0.0
        var teamTwoOpponentWinPert = 0.0

        log.info("{} - {}", teamOneOpponentList.size, teamTwoOpponentList.size)

        teamOneOpponentList.forEach { game ->
            val teamOneWon: Boolean
            log.debug(game.gameId)
            val opponentRecord: Standing = if (game.homeTeam.name == teamName) {
                teamOneWon = game.homeScore > game.awayScore
                standingService.findStandingByTeam(game.awayTeam.name)
            } else {
                teamOneWon = game.awayScore > game.homeScore
                standingService.findStandingByTeam(game.homeTeam.name)
            }
             if (teamOneWon)
                teamOneOpponentWinPert += opponentRecord.winPercentage
            log.debug("{}; Win percentage {}; Opponent Record: {}", teamName, teamOneOpponentWinPert, opponentRecord)
        }

        teamTwoOpponentList.forEach { game ->
            val teamTwoWon: Boolean
            log.debug(game.gameId)
            val opponentRecord: Standing = if( game.homeTeam.name == teamName2) {
                teamTwoWon = game.homeScore > game.awayScore
                standingService.findStandingByTeam(game.awayTeam.name)
            } else {
                teamTwoWon = game.awayScore > game.homeScore
                standingService.findStandingByTeam(game.homeTeam.name)
            }
            if (teamTwoWon)
                teamTwoOpponentWinPert += opponentRecord.winPercentage
            log.debug("{}; Win percentage {}; Opponent Record: {}", teamName2, teamTwoOpponentWinPert, opponentRecord)
        }

        val teamOneOpponentWinPertAvg = teamOneOpponentWinPert / teamOneOpponentList.size
        val teamTwoOpponentWinPertAvg = teamTwoOpponentWinPert / teamTwoOpponentList.size

        log.info("Team {}: {}; Team {}: {}", teamName, teamOneOpponentWinPertAvg, teamName2, teamTwoOpponentWinPertAvg)
        return if(teamOneOpponentWinPertAvg > teamTwoOpponentWinPertAvg) {
            1
        } else if(teamOneOpponentWinPertAvg < teamTwoOpponentWinPertAvg) {
            -1
        } else {
            0
        }
    }

    fun headToHead(teamName: String?, teamName2: String?): Int {
        val gameName = teamName.plus(" vs ").plus(teamName2)
        val gameNameTwo = teamName2.plus(" vs ").plus(teamName)

        log.info("{} vs {}", teamName, teamName2)
        val gameOne = gameService.findGameByName(gameName)
        val gameTwo = gameService.findGameByName(gameNameTwo)

        if((gameOne != null && gameOne.time == "Final") || (gameTwo != null && gameTwo.time == "Final")) {
            if (gameOne != null) {
                return if(gameOne.homeScore > gameOne.awayScore) {
                    1
                } else {
                    -1
                }
            }
            if (gameTwo != null) {
                return if(gameTwo.homeScore > gameTwo.awayScore) {
                    -1
                } else {
                    1
                }
            }
        }

        return 0
    }

    @PostMapping("season/start")
    fun startSeason(@RequestBody teamNames: List<String>, @RequestParam year: Year, @RequestParam season: String) {
        for (team in teamNames) {
            try {
                val currentTeam = teamService.findTeamByName(team)
                if(currentTeam.isActive) {
                    log.warn("Team {} is already active for {}-{} season", team, season, year)
                } else {
                    log.debug("Activating team {} for the {}-{} season", team, season, year)
                    currentTeam.isActive = true
                    val newRoster = Roster()
//                team.rosterList += newRoster
                    currentTeam.rosterMap += mapOf(Pair(season, year) to newRoster)
//                team.rosterMap += mapOf("spr-2024" to newRoster)
                    rosterService.save(newRoster)
                    teamService.save(currentTeam)
                }
            } catch (e: Exception) {
                log.error("Error occurred activating teams: team: {}; error {}", team, e.stackTrace)
                throw Exception(e)
            }
        }
    }

    @PostMapping("season/end")
    fun endSeason() {
        val allActiveTeam = teamService.findTeamsByIsActive(true)

        for (team in allActiveTeam) {
            try {
                team.isActive = false
                teamService.save(team)
            } catch (e: Exception) {
                log.error("Error occurred deactivating teams: team: {}; error {}", team.name, e.stackTrace)
                throw Exception(e)
            }
        }
    }
}