package nl.ronalddehaan

import org.apache.kafka.streams.kstream.{ForeachAction, ValueMapper}

object HelperFunctions {

  implicit def functionToForeachAction[K, V](f: (K, V) ⇒ Unit): ForeachAction[K, V] =
    (key: K, value: V) ⇒ f(key, value)

  implicit def functionToMapValues[V, VR](f: (V) ⇒ VR): ValueMapper[V, VR] =
    (value: V) ⇒ f(value)

}
