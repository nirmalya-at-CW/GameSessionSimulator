package com.huddle.NFL.loadTest.simulations.gameSessionRecorder.ExchangeEntities

/**
  * Created by nirmalya on 23/8/17.
  */
object Responses {

  case class ExpandedMessage (successId: Int, description: String)
  case class Supplementary(dataCarried: Map[String,String])

  case class RESPGameSessionBody(opSuccess: Boolean, message: ExpandedMessage, contents: Option[Map[String,String]]=None)

}
