package com.dmfl.backendserver.controller

import com.dmfl.backendserver.model.*
import com.dmfl.backendserver.service.*
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Comparator.comparing
import java.util.NoSuchElementException

@RestController
@RequestMapping("/api")
class DmflController(private val teamService: TeamService, private val playerService: PlayerService,
                     private val scheduleService: ScheduleService, private val gameService: GameService,
                     private val standingService: StandingService) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/teams")
    fun getTeams(): List<Team> {

        return teamService
            .findAllTeams()
    }

    @PostMapping("/teams/save")
    fun addTeam(@RequestBody team: Team) {
        for(player in team.players){
            if(player.name != null){
                try {
                    playerService.findPlayerByName(player.name!!)
                } catch (e: NoSuchElementException) {
                    log.warn("Player does not exist, creating new player")
                    playerService.save(player)
                } catch (e: Exception) {
                    log.error("Error occurred while saving team: {}", e.stackTrace)
                    throw Exception(e)
                }
            }
        }
        teamService.save(team)
    }

    @PostMapping("/teams/addPlayer")
    fun addPlayerToTeam(@RequestParam teamName: String, @RequestBody player: Player): ResponseEntity<String> {

        try {
            val team = teamService.findTeamByName(teamName)
            if(team != null) {
                log.info("Adding player {} to Team {}", player.name, team.name)
                val arrayList = ArrayList(team.players)
                arrayList.add(player)
                team.players = arrayList;
                teamService.save(team)
                return ResponseEntity(HttpStatus.OK)
            }
            log.warn("Player {} can not be added to non-existent team {}", player.name, teamName)
            return ResponseEntity("Team doesn't exist.", HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            log.error("Error occurred while adding player to team: {}", e.stackTrace)
            throw Exception(e)
        }
    }

    @GetMapping("/teams/{name}")
    fun findTeam(@PathVariable name: String): Team? {
        log.info("Team name: {}", name)

        return teamService.findTeamByName(name)
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
                gameService.findGameByName(game.name)
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
                if(it.name == game.name) {
                    gameExist = true
                }
            }

            if(!schedule.current || !gameExist) {
                log.warn("Not current week or game doesn't exist")
            }

            if(game.time == "Final" && gameExist && !game.isPlayoff) {
                val teams = StringUtils.splitByWholeSeparator(game.name, " vs ")

                log.info("{} vs {}", teams[0], teams[1])
                val homeTeamStanding = standingService.findStandingByTeam(teams[0])
                val awayTeamStanding = standingService.findStandingByTeam(teams[1])
                var homeWins = homeTeamStanding.wins
                var homeLosses = homeTeamStanding.losses
                var homeStreak = homeTeamStanding.streak
                var awayWins = awayTeamStanding.wins
                var awayLosses = awayTeamStanding.losses
                var awayStreak = awayTeamStanding.streak
                if(game.homeScore > game.awayScore) {
                    ++homeWins
                    ++awayLosses
                    ++homeStreak
                    awayStreak = 0
                } else {
                    ++homeLosses
                    ++awayWins
                    homeStreak = 0
                    ++awayStreak
                }

                homeTeamStanding.wins = homeWins
                homeTeamStanding.losses = homeLosses
                homeTeamStanding.streak = homeStreak
                homeTeamStanding.winPercentage = (homeWins / (homeWins + homeLosses))
                homeTeamStanding.pointsFor += game.homeScore
                homeTeamStanding.pointsAgainst += game.awayScore
                homeTeamStanding.pointDifferential += (game.homeScore - game.awayScore)
                awayTeamStanding.wins = awayWins
                awayTeamStanding.losses = awayLosses
                awayTeamStanding.streak = awayStreak
                awayTeamStanding.winPercentage = awayWins / (awayWins + awayLosses)
                awayTeamStanding.pointsFor += game.awayScore
                awayTeamStanding.pointsAgainst += game.homeScore
                awayTeamStanding.pointDifferential += (game.awayScore - game.homeScore)
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

        return group.sortedWith(
            comparing<Standing?, String?>(
                { it.teamName },
                { t1, t2 -> (strengthOfVictory(t1, t2))}
            ).thenComparing(
                { it.pointDifferential },
                { pd1, pd2 -> (pd1.compareTo(pd2))},
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
            val teams = StringUtils.splitByWholeSeparator(game.name, " vs ")
            val opponentRecord: Standing = if(teams[0] == teamName) {
                standingService.findStandingByTeam(teams[1])
            } else {
                standingService.findStandingByTeam(teams[0])
            }
            teamOneOpponentWinPert += opponentRecord.winPercentage
        }

        teamTwoOpponentList.forEach { game ->
            val teams = StringUtils.splitByWholeSeparator(game.name, " vs ")
            val opponentRecord: Standing = if(teams[0] == teamName) {
                standingService.findStandingByTeam(teams[1])
            } else {
                standingService.findStandingByTeam(teams[0])
            }
            teamTwoOpponentWinPert += opponentRecord.winPercentage
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
}