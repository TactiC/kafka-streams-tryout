package nl.ronalddehaan.akka_stream_kafka


import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.pattern.ask
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import nl.ronalddehaan.Enricher
import nl.ronalddehaan.Enricher.Process
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

object Stream extends App {

  val topic = "test-topic"
  val result = "result-topic"

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(30 seconds)

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val enricher = system.actorOf(Enricher.props(), Enricher.name)

  Consumer.committableSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(8) { msg =>
        enricher.ask(Process(msg.record.value()))
        msg.committableOffset.commitScaladsl()
      }
    .runWith(Sink.ignore)


}
