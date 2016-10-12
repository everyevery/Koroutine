package net.everyevery.koroutine.channel

import akka.actor.ActorRef


abstract class GenChannel[A] {
  def get(implicit self: ActorRef): A
  def put(a: A)(implicit self: ActorRef): Unit
}

class Channel[A](size: Int) extends GenChannel[A] {
  val actor: ActorRef = ChannelActor(size)

  override def get(implicit self: ActorRef): Unit = actor.tell(GetMessage, self)

  override def put(a: A)(implicit self: ActorRef): Unit = actor.tell(PutMessage(a), self)
}

object Channel {
  def apply[A](size: Int): Channel[A] = {
    new Channel[A](size)
  }
}
