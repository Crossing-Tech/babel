package io.xtech.cf.babel.camel.endpoints

import org.apache.camel.ExchangePattern
import Mapper

class direct0(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
}

class DirectProducer(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectProducer03 = {
    _option(s"toto=$toto")
    new DirectProducer03(endpoint, options)
  }
  def exchangePattern(exchangePattern: ExchangePattern): DirectProducer02 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectProducer02(endpoint, options)
  }
  def block(block: Boolean): DirectProducer01 = {
    _option(s"block=$block")
    new DirectProducer01(endpoint, options)
  }
}
class DirectProducer03(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def exchangePattern(exchangePattern: ExchangePattern): DirectProducer0203 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectProducer0203(endpoint, options)
  }
  def block(block: Boolean): DirectProducer0103 = {
    _option(s"block=$block")
    new DirectProducer0103(endpoint, options)
  }
}
class DirectConsumer0103(val endpoint: String, var options: Option[String]) extends PartialConnector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def exchangePattern(exchangePattern: ExchangePattern): DirectConsumer010203 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectConsumer010203(endpoint, options)
  }
}
class DirectConsumer0102(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectConsumer010203 = {
    _option(s"toto=$toto")
    new DirectConsumer010203(endpoint, options)
  }
}
class DirectConsumer01(val endpoint: String, var options: Option[String]) extends PartialConnector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectConsumer0103 = {
    _option(s"toto=$toto")
    new DirectConsumer0103(endpoint, options)
  }
  def exchangePattern(exchangePattern: ExchangePattern): DirectConsumer0102 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectConsumer0102(endpoint, options)
  }
}
class DirectConsumer(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectConsumer03 = {
    _option(s"toto=$toto")
    new DirectConsumer03(endpoint, options)
  }
  def exchangePattern(exchangePattern: ExchangePattern): DirectConsumer02 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectConsumer02(endpoint, options)
  }
  def block(block: Boolean): DirectConsumer01 = {
    _option(s"block=$block")
    new DirectConsumer01(endpoint, options)
  }
}
class DirectProducer0203(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def block(block: Boolean): DirectProducer010203 = {
    _option(s"block=$block")
    new DirectProducer010203(endpoint, options)
  }
}
class DirectProducer02(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectProducer0203 = {
    _option(s"toto=$toto")
    new DirectProducer0203(endpoint, options)
  }
  def block(block: Boolean): DirectProducer0102 = {
    _option(s"block=$block")
    new DirectProducer0102(endpoint, options)
  }
}
class DirectConsumer010203(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
}
class DirectConsumer03(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def exchangePattern(exchangePattern: ExchangePattern): DirectConsumer0203 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectConsumer0203(endpoint, options)
  }
  def block(block: Boolean): DirectConsumer0103 = {
    _option(s"block=$block")
    new DirectConsumer0103(endpoint, options)
  }
}
class DirectProducer01(val endpoint: String, var options: Option[String]) extends PartialConnector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectProducer0103 = {
    _option(s"toto=$toto")
    new DirectProducer0103(endpoint, options)
  }
  def exchangePattern(exchangePattern: ExchangePattern): DirectProducer0102 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectProducer0102(endpoint, options)
  }
}
class DirectConsumer0203(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def block(block: Boolean): DirectConsumer010203 = {
    _option(s"block=$block")
    new DirectConsumer010203(endpoint, options)
  }
}
class DirectProducer0103(val endpoint: String, var options: Option[String]) extends PartialConnector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def exchangePattern(exchangePattern: ExchangePattern): DirectProducer010203 = {
    val mapped = Mapper.getExchangePattern(exchangePattern)
    _option(s"exchangePattern=$mapped")
    new DirectProducer010203(endpoint, options)
  }
}
class DirectProducer010203(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
}
class DirectConsumer02(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectConsumer0203 = {
    _option(s"toto=$toto")
    new DirectConsumer0203(endpoint, options)
  }
  def block(block: Boolean): DirectConsumer0102 = {
    _option(s"block=$block")
    new DirectConsumer0102(endpoint, options)
  }
}
class DirectProducer0102(val endpoint: String, var options: Option[String]) extends Connector {
  def option(key: String, value: Any): direct0 = {
    _option(s"$key=$value")
    new direct0(endpoint, options)
  }
  val component = "direct"
  def toto(toto: Long): DirectProducer010203 = {
    _option(s"toto=$toto")
    new DirectProducer010203(endpoint, options)
  }
}

