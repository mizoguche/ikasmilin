package info.mizoguche.ikasmilin

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import com.google.gson.Gson
import kotlin.system.exitProcess

data class Results(val summary: Summary, val unique_id: String, val results: List<IkasumiResult>)
data class Summary(val victory_rate: Float, val defeat_count: Int, val death_count_average: Float, val count: Int, val special_count_average: Float, val kill_count_average: Float, val victory_count: Int, val assist_count_average: Float)
data class IkasumiResult(
        val estimate_gachi_power: Int,
        val stage: Stage,
        val other_team_count: Int,
        val start_time: Long,
        val game_mode: GameMode,
        val weapon_paint_point: Long,
        val elapsed_time: Int,
        val my_team_result: TeamResult,
        val my_team_count: Int,
        val player_result: PlayerResult,
        val battle_number: Int,
        val rule: Rule,
        val udemae: Udemae,
        val star_rank: Int,
        val player_rank: Int,
        val other_team_result: TeamResult,
        val type: String

)

data class Stage(val image: String, val name: String, val id: String)
data class GameMode(val key: String, val name: String)
data class TeamResult(val name: String, val key: String)
data class PlayerResult(
        val sort_score: Int,
        val player: Player,
        val udemae: Udemae,
        val star_rank: Int,
        val clothes: Gear,
        val principal_id: String,
        val shoes: Gear,
        val weapon: Weapon,
        val head: Gear,
        val shoes_skills: Skills,
        val clothes_skills: Skills,
        val nickname: String,
        val player_rank: Int,
        val assist_count: Int,
        val special_count: Int,
        val game_paint_point: Int,
        val death_count: Int,
        val kill_count: Int
)

data class Player(
        val head_skills: Skills
)

data class Skills(val main: Skill, val subs: List<Skill?>)
data class Skill(val name: String, val image: String, val id: String)
data class Udemae(val number: Int, val name: String, val s_plus_number: Int?)
data class Gear(val thumbnail: String, val rarity: Int, val brand: Brand)
data class Brand(val id: String, val name: String, val image: String, val frequent_skill: Skill, val kind: String)
data class Weapon(val thumbnail: String, val sub: SubWeapon, val id: String, val special: SpecialWeapon, val name: String, val image: String)
data class SubWeapon(val name: String, val image_a: String, val id: String, val image_b: String)
data class SpecialWeapon(val image_a: String, val name: String, val id: String, val image_b: String)
data class Rule(val key: String, val multiline_name: String, val name: String)

fun requestResults(iksmSession: String): Request = "https://app.splatoon2.nintendo.net/api/results"
        .httpGet()
        .header(Pair("Cookie", "iksm_session=$iksmSession"))

fun requestResults(iksmSession: String, callback: (Results) -> Unit) {
    requestResults(iksmSession)
            .responseString { _, _, result ->
                val results = Gson().fromJson(result.get(), Results::class.java)
                callback(results)
            }
}

fun requestResultsAsJson(iksmSession: String, callback: (String) -> Unit) {
    requestResults(iksmSession)
            .responseString { _, _, result ->
                when(result) {
                    is Result.Failure -> {
                        println("Failure: ${result.error}")
                    }
                    is Result.Success -> {
                        callback(result.get())
                    }
                }
            }
}

fun main(args: Array<String>) {
    requestResultsAsJson(args.first()) {
        println(it)
        exitProcess(0)
    }
}
