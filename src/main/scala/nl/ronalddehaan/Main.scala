package nl.ronalddehaan

import java.util.Properties

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import nl.ronalddehaan.Enricher.Process
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}

import scala.concurrent.{Await, Future}


object Main extends App with StreamConfig {

  val topic = "test-topic"
  val result = "result-topic"

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(30 seconds)

  val enricher = system.actorOf(Enricher.props(), Enricher.name)


 // Stream shizzle is entitled to have its own class
  val builder = new StreamsBuilder()
  builder.stream(topic)
    .foreach((k: String, v: String) => process(v,k))
//    .map[String, String]((k: String, v: String) => mapper(k,v))
//    .mapValues[String]((v: String) => foo(v))
//    .to(result)

  val stream = new KafkaStreams(builder.build(), config)
  stream.cleanUp()
  stream.start()

  def process(k: String, v: String) = {
    enricher.ask(Process(k))
  }

//  def foo(value: String): String = {
//    enricher.ask(Process(value))
//    Await.result(future, 30 seconds).asInstanceOf[String]
//  }

  def mapper(k: String, v: String) = {
    new KeyValue[String, String](k,v)
  }
}

trait StreamConfig {
    val config = new Properties {
      put(StreamsConfig.APPLICATION_ID_CONFIG, "doodles")
      put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      // Specify default (de)serializers for record keys and for record values.
      put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
      put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    }
}
