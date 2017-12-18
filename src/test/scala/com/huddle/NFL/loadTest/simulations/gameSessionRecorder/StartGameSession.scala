package com.huddle.NFL.loadTest.simulations.gameSessionRecorder

import com.huddle.NFL.loadTest.simulations.gameSessionRecorder.ExchangeEntities.Requests._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.json4s.native.Serialization
import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import org.json4s.{ShortTypeHints, native}


/**
  * Created by nirmalya on 17/8/17.
  */


class ScenarioDriver extends Simulation {

  import org.json4s.DefaultFormats

  val conf = ConfigFactory.load()

  implicit val serialization = native.Serialization
  implicit val formats       = Serialization.formats(ShortTypeHints(List(classOf[REQStartAGameWith])))

  val gameSessionRecorderServerHost = conf.getConfig("GameSession.availableAt").getString("host")
  val gameSessionRecorderServerPort = conf.getConfig("GameSession.availableAt").getInt("port")


  println(s"GameSession Server is expected to be at: ${gameSessionRecorderServerHost}:${gameSessionRecorderServerPort}")

  val headers_10 = Map("Content-Type" -> "application/json") // Note the headers specific to a given request

  val httpConf = http
    .baseURL(s"http://${gameSessionRecorderServerHost}:${gameSessionRecorderServerPort}") // Here is the root for all relative URLs
    //.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .headers(headers_10)

  val scn = scenario("Play a complete Game") // A scenario is a chain of requests and pauses
      .feed(csv("GameSessionPreparePlayPausePlaySeq.csv"))
      .exec(session => {
        val r = REQStartAGameWith(
          session("companyID")         .validate[String].get,
          session("departmentID")      .validate[String].get,
          session("gameID")            .validate[String].get,
          session("playerID")          .validate[String].get,
          session("gameType")          .validate[String].get,
          session("groupID")           .validate[String].get,
          session("gameName")          .validate[String].get,
          session("gameSessionUUID")   .validate[String].get,
          session("playedInTimezone")  .validate[String].get
        )
        session
          .set("req", r.jsonify)
          .set("pauseDuration1", session("pause1").validate[String].get.toInt)
          .set("endReason",      session("end")   .validate[String].get)

      })
      .exec(

            http("Creation")
              .post("/start")
              .body(StringBody("${req}"))
              .check(jsonPath("$.contents.gameSessionID")
                .saveAs("sessionID"))

      )
      .exec(session => {
        val r = REQSetQuizForGameWith(
          session("sessionID").validate[String].recover("NoSessionID").get,
          session("prepare").validate[String].get
        )

        session.set("req", r.jsonify)
      })
     .exec(http("Preparation")
       .post("/prepare")
       .body(StringBody("${req}"))
       .transformResponse {
         case response if response.isReceived =>

           println("response =" + response.body.string)
           (response)

       }
       .check(jsonPath("$.opSuccess").ofType[Boolean].is(true))
     )

    .exec(session => {
        val playParamsSlashSeparated = session("play1").validate[String].get
        val playParams = playParamsSlashSeparated.split("/").toIndexedSeq
        val r = REQPlayAGameWith(
          session("sessionID").validate[String].get,
          playParams(0), // questionID
          playParams(1), // answerID
          (if (playParams(2) == "true" ) true else false), // isCorrect
          playParams(3).toInt, // score
          playParams(4).toInt // timeTakenToAnswerAtFE
        )

        session.set("req", r.jsonify)
      }
    )
    .exec(
        http("Answer First Question")
        .post("/play")
        .body(StringBody("${req}"))
        .check(jsonPath("$.opSuccess").ofType[Boolean].is(true))

    )
    .pause("${pauseDuration1}")
    .exec(session => {
        val playParamsSlashSeparated = session("play2").validate[String].get
        val playParams = playParamsSlashSeparated.split("/").toIndexedSeq
        val r = REQPlayAGameWith(
          session("sessionID").validate[String].get,
          playParams(0), // questionID
          playParams(1), // answerID
          (if (playParams(2) == "true") true else false), // isCorrect
          playParams(3).toInt, // score
          playParams(4).toInt // timeTakenToAnswerAtFE
        )
        session.set("req", r.jsonify)
      }
    )
    .exec(
        http("Answer Second Question")
          .post("/play")
          .body(StringBody("${req}"))
          .check(jsonPath("$.opSuccess").ofType[Boolean].is(true))

    )
    .exec(session => {
        val playParamsSlashSeparated = session("play3").validate[String].get
        val playParams = playParamsSlashSeparated.split("/").toIndexedSeq
        val r = REQPlayAGameWith(
          session("sessionID").validate[String].get,
          playParams(0), // questionID
          playParams(1), // answerID
          (if (playParams(2) == "true") true else false), // isCorrect
          playParams(3).toInt, // score
          playParams(4).toInt // timeTakenToAnswerAtFE
        )
        session.set("req", r.jsonify)
      }
    )
    .exec(
      http("Answer Third Question")
        .post("/play")
        .body(StringBody("${req}"))
        .check(jsonPath("$.opSuccess").ofType[Boolean].is(true))

    )
    .exec(session => {

          val payload = s"sessionID:${session("sessionID")}"
          val endReason = session {"endReason"} // This value is already captured, when the first CSV line is read

          if (endReason.as[String].get == "byPlayer") {
            val r = REQEndAGameWith(
              session("sessionID").validate[String].get,
              20 // Total time taken at the FE, arbitrary for our purposes now
            )
            session.set("req", r.jsonify)
          }
          else if (endReason.as[String].get == "byManager") {
            val r = REQEndAGameByManagerWith(
              session("sessionID").validate[String].get,
              session("manager").validate[String].get
            )
            session.set("req", r.jsonify)
          }
          else
            session.set("req", "")
    })
    .doSwitch(session => session("endReason").as[String]) (

       "byPlayer"    -> exec(http("End game session by player")
                             .post("/end")
                             .body(StringBody("${req}"))
                          ),
       "byManager"   -> exec(http("End game session by Manager")
                             .post("/endByManager")
                             .body(StringBody("${req}"))
                           )

    )

  val separationDuration = Duration(10, TimeUnit.SECONDS)
  setUp(scn.inject(
                    nothingFor(4 seconds),
                    atOnceUsers(10),
                    splitUsers(1000) into(rampUsers(10) over(10 seconds)) separatedBy(10 seconds)

                  ).protocols(httpConf)
  )
}
