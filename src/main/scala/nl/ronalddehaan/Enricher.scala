package nl.ronalddehaan
import scala.concurrent.duration._
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import nl.ronalddehaan.Enricher.Process
import nl.ronalddehaan.akka_stream_kafka.FeatureProducer
import nl.ronalddehaan.akka_stream_kafka.FeatureProducer.Message

object Enricher {
  def props() = Props[Enricher]()
  def name = "enricher"

  case class Process(msg: String)
}

class Enricher extends Actor {

  implicit val timeout = Timeout(30 seconds)
  val producer = context.actorOf(FeatureProducer.props(), FeatureProducer.name)

  override def receive: Receive = {
    case Process(msg) => producer.ask(Message(s"$msg-enriched"))
  }

}
