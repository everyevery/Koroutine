package net.everyevery.koroutine.channel

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.immutable.Queue

/**
  * Created by jongsoo.lee on 2016. 10. 13..
  */
sealed trait ChannelMessage
case object GetMessage extends ChannelMessage
case class PutMessage[A](value: A) extends ChannelMessage
case object AckMessage extends ChannelMessage
case class AnsMessage[A](value: A) extends ChannelMessage

private[channel] class ChannelActor[A](val size: Int) extends Actor {

  var buffer: Queue[(ActorRef,A)] = Queue.empty
  var receivers: Queue[ActorRef] = Queue.empty

  override def receive: Receive = {
    case GetMessage =>
      receivers = receivers :+ sender
      if (buffer.nonEmpty)
        publish()
    case m: PutMessage[A] =>
      buffer  = buffer :+ (sender, m.value)
      if (receivers.nonEmpty)
        publish()
  }

  private def publish(): Unit = {
    if (buffer.nonEmpty && receivers.nonEmpty) {
      val ((s,v),svq) = buffer.dequeue
      val (w,wq) = receivers.dequeue
      buffer = svq
      receivers = wq
      s.tell(AckMessage, self)
      w.tell(AnsMessage(v), self)
    }
  }
}

object ChannelActor {
  def apply[A](size: Int)(implicit system: ActorSystem): ActorRef = {
    system.actorOf(Props(classOf[ChannelActor[A]], size))
  }
}
