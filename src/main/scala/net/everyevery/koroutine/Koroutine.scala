package net.everyevery.koroutine

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import net.everyevery.koroutine.channel.Channel

sealed trait KoroutineMessage
case object MainKoroutineMessage extends KoroutineMessage


class KoroutineActor(val receive: Receive) extends Actor

object Koroutine {

  implicit private val system: ActorSystem = ActorSystem("Koroutine")

  def go(receive: Receive): Unit = {
    system.actorOf(Props(classOf[KoroutineActor], receive)).tell(MainKoroutineMessage, Actor.noSender)
  }

  def main(args: Array[String]): Unit = {
    val channel: Channel[Int] = Channel(1)
    go {
      case MainKoroutineMessage =>
        channel.get
    }
  }
}
