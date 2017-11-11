package nl.ronalddehaan.akka_stream_kafka

import akka.actor.{Actor, Props}
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import nl.ronalddehaan.akka_stream_kafka.FeatureProducer.Message
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

object FeatureProducer {
  def props() = Props[FeatureProducer]()
  def name = "producer"
  case class Message(msg: String)
}

class FeatureProducer extends Actor {

  implicit val materializer = ActorMaterializer()
  val producerSettings = ProducerSettings(context.system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")
  val kafkaProducer = producerSettings.createKafkaProducer()

  override def receive: Receive = {
    case Message(msg) => produceRecord(msg)
  }

  def produceRecord(msg: String) = {
    kafkaProducer.send(new ProducerRecord[Array[Byte], String]("result-topic", msg))
  }
}
