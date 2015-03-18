/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.TransactionSpec.TransactionTestContext
import io.xtech.babel.camel.test.CachedBabelSpringSpecification
import javax.sql.DataSource
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.spring.SpringCamelContext
import org.springframework.context.annotation.{ AnnotationConfigApplicationContext, Bean, Configuration }
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.{ EmbeddedDatabaseFactoryBean, EmbeddedDatabaseType }
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

object TransactionSpec {

  @Configuration
  class TransactionTestContext {

    val resourceLoader = new DefaultResourceLoader

    @Bean(name = Array("dataSource"))
    def embeddedDatabaseFactory = {
      val db = new EmbeddedDatabaseFactoryBean
      db.setDatabaseType(EmbeddedDatabaseType.H2)

      val populator = new ResourceDatabasePopulator
      populator.addScript(resourceLoader.getResource("classpath:schema.sql"))
      db.setDatabasePopulator(populator)
      db
    }

    @Bean
    def txManager(dataSource: DataSource) : DataSourceTransactionManager = {
      new DataSourceTransactionManager(dataSource)
    }

    @Bean
    def camelContext: ModelCamelContext = new SpringCamelContext

    @Bean
    def jdbcTemplate(dataSource: DataSource): JdbcTemplate = new JdbcTemplate(dataSource)

  }

}

/**
  * Integration test that tests the transaction keyword with Apache Camel.
  */
class TransactionSpec extends CachedBabelSpringSpecification {
  sequential

  type ContextType = AnnotationConfigApplicationContext

  lazy val applicationContext = new AnnotationConfigApplicationContext(classOf[TransactionTestContext])

  "CamelDSL" should {

    "support transaction and commit" in {

      import io.xtech.babel.camel.builder.RouteBuilder

      val jdbcTemplate = bean[JdbcTemplate]

      jdbcTemplate.execute("delete from users")

      val routeDef = new RouteBuilder {
        from("direct:input").transacted(). //defines that the messages are in a transaction
          to("sql:insert into users (name) values (#)?dataSourceRef=dataSource").
          to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input", "toto")

      val numUsers = jdbcTemplate.queryForObject("select count(*) from Users", classOf[Integer])

      numUsers mustEqual 2
    }

    "support transaction and rollback" in {

      import io.xtech.babel.camel.builder.RouteBuilder

      val jdbcTemplate = bean[JdbcTemplate]

      jdbcTemplate.execute("delete from users")

      val routeDef = new RouteBuilder {
        from("direct:input2").transacted.to("sql:insert into users (name) values (#)?dataSourceRef=dataSource").as[String].process(m => throw new Exception("Expected exception")).to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input2", "toto") must throwA[Exception]

      val numUsers = jdbcTemplate.queryForObject("select count(*) from Users", classOf[Integer])

      numUsers mustEqual 0
    }

    "partial commit without transaction" in {
      import io.xtech.babel.camel.builder.RouteBuilder

      val jdbcTemplate = bean[JdbcTemplate]

      jdbcTemplate.execute("delete from users")

      val routeDef = new RouteBuilder {
        from("direct:input3").to("sql:insert into users (name) values (#)?dataSourceRef=dataSource").as[String].process(m => throw new Exception("Expected exception")).to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input3", "toto") must throwA[Exception]

      val numUsers = jdbcTemplate.queryForObject("select count(*) from Users", classOf[Integer])

      numUsers mustEqual 1
    }

    "provide transaction error handler " in {

      import io.xtech.babel.camel.builder.RouteBuilder

      val jdbcTemplate = bean[JdbcTemplate]

      jdbcTemplate.execute("delete from users")

      val routeDef = new RouteBuilder {
        var tries = 3

        from("direct:input6")
          .handle(_.transactionErrorHandler.maximumRedeliveries(tries))
          .transacted
          .to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
          .to("mock:in-the-middle-route")
          .as[String].process(m => {
            tries -= 1
            if (tries != 0) throw new Exception("Expected exception") else m
          })
          .to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
          .to("mock:final-route")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:in-the-middle-route", classOf[MockEndpoint])
      val mockEndpointFinal = camelContext.getEndpoint("mock:final-route", classOf[MockEndpoint])
      mockEndpoint.setExpectedMessageCount(1)
      mockEndpointFinal.setExpectedMessageCount(1)

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input6", "toto")

      val numUsers = jdbcTemplate.queryForObject("select count(*) from Users", classOf[Integer])

      mockEndpoint.assertIsSatisfied()
      numUsers mustEqual 2
    }

    "provide transaction error handler in RouteBuilder" in {

      import io.xtech.babel.camel.builder.RouteBuilder

      val jdbcTemplate = bean[JdbcTemplate]

      jdbcTemplate.execute("delete from users")

      val routeDef = new RouteBuilder {
        var tries = 3

        handle(_.transactionErrorHandler.maximumRedeliveries(tries))

        from("direct:input7")
          .transacted
          .to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
          .to("mock:in-the-middle-route-builder")
          .as[String].process(m => {
            tries -= 1;
            if (tries != 0) throw new Exception("Expected exception") else m
          })
          .to("sql:insert into users (name) values (#)?dataSourceRef=dataSource")
          .to("mock:final-route-builder")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:in-the-middle-route-builder", classOf[MockEndpoint])
      val mockEndpointFinal = camelContext.getEndpoint("mock:final-route-builder", classOf[MockEndpoint])
      mockEndpoint.setExpectedMessageCount(1)
      mockEndpointFinal.setExpectedMessageCount(1)

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input7", "toto")

      val numUsers = jdbcTemplate.queryForObject("select count(*) from Users", classOf[Integer])

      mockEndpoint.assertIsSatisfied()
      numUsers mustEqual 2
    }

  }

}
