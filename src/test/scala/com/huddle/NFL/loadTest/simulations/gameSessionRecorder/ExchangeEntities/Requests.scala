package com.huddle.NFL.loadTest.simulations.gameSessionRecorder.ExchangeEntities

import org.json4s.DefaultFormats
import org.json4s.native.Json

/**
  * Created by nirmalya on 23/8/17.
  */
object Requests {

  case class REQStartAGameWith(
                                companyID: String, departmentID: String, gameID: String,
                                playerID: String,  gameType: String, groupID: String ,
                                gameName: String, gameSessionUUID: String, playedInTimezone: String)
   {
    override def toString =
      new StringBuffer().append(companyID)      .append(".")
        .append(companyID)      .append(".")
        .append(playerID)       .append(".")
        .append(gameID)         .append(".")
        .append(gameName)       .append(".")
        .append(gameSessionUUID)
        .toString

    def jsonify = Json(DefaultFormats).write[REQStartAGameWith](this)
  }
  case class REQSetQuizForGameWith(sessionID: String, questionMetadata: String ) {
    def jsonify = Json(DefaultFormats).write[REQSetQuizForGameWith](this)
  }
  case class REQPlayAGameWith(sessionID: String, questionID: String, answerID: String, isCorrect: Boolean, points: Int, timeSpentToAnswerAtFE: Int) {
    def jsonify = Json(DefaultFormats).write[REQPlayAGameWith](this)
  }
  case class REQPlayAClipWith(sessionID: String, clipName: String) {
    def jsonify = Json(DefaultFormats).write[REQPlayAClipWith](this)
  }
  case class REQPauseAGameWith(sessionID: String) {
    def jsonify = Json(DefaultFormats).write[REQPauseAGameWith](this)
  }
  case class REQEndAGameWith(sessionID: String, totalTimeTakenByPlayerAtFE: Int) {
    def jsonify = Json(DefaultFormats).write[REQEndAGameWith](this)
  }
  case class REQEndAGameByManagerWith(sessionID: String, managerName: String) {
    def jsonify = Json(DefaultFormats).write[REQEndAGameByManagerWith](this)
  }
}
