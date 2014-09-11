import io.xtech.cf.babel.camel.endpoints.DirectConsumer
import io.xtech.cf.babel.camel.endpoints.DirectProducer
package object endpoints {
  object direct {
    def consumer(endpoint: String) = new DirectConsumer(endpoint, None)
    implicit def directConsumerToSource(consumer: DirectConsumer): String = consumer.uri
    def producer(endpoint: String) = new DirectProducer(endpoint, None)
    implicit def directProducerToSink(producer: DirectProducer): String = producer.uri
  }
}