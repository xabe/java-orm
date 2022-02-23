package com.xabe.mybatis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import com.xabe.mybatis.mapper.Event;
import com.xabe.mybatis.mapper.EventMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EventMapperTest {

  private final Event event = Event.builder().name("ACCOUNT:CREATED").body("1").build();

  private static SqlSessionFactory factory = null;

  @BeforeAll
  public static void setup() throws IOException {
    final Properties properties = new Properties();
    properties.setProperty("driverClassName", "org.mariadb.jdbc.Driver");
    properties.setProperty("jdbcUrl", "jdbc:mariadb://localhost:3306/demo");
    properties.setProperty("username", "user");
    properties.setProperty("password", "password");
    final DataSource dataSource = new HikariDataSource(new HikariConfig(properties));
    final JdbcTransactionFactory jdbcTransactionFactory = new JdbcTransactionFactory();
    final Environment environment = new Environment("dev", jdbcTransactionFactory, dataSource);
    final Configuration configuration = new Configuration(environment);
    configuration.getTypeAliasRegistry().registerAlias(Event.class);
    configuration.addMapper(EventMapper.class);
    factory = new SqlSessionFactoryBuilder().build(configuration);
    try (final SqlSession session = factory.openSession()) {
      final EventMapper eventMapper = session.getMapper(EventMapper.class);
      eventMapper.dropTable("event");
      eventMapper.createTableIfNotExist("event");
      session.commit();
    }
  }

  @Test
  @Order(1)
  public void shouldCreateEvent() throws Exception {

    try (final SqlSession session = factory.openSession()) {
      final EventMapper mapper = session.getMapper(EventMapper.class);
      mapper.create(this.event);
      session.commit();
      final List<Event> result = mapper.getNonSentEvents();
      assertThat(result, is(notNullValue()));
      assertThat(result, is(hasSize(1)));
    }
  }

  @Test
  @Order(2)
  public void shouldMarkSentEvent() throws Exception {

    try (final SqlSession session = factory.openSession()) {
      final EventMapper mapper = session.getMapper(EventMapper.class);
      mapper.markAsSent(this.event.getId(), LocalDateTime.now());
      session.commit();
      final List<Event> result = mapper.getNonSentEvents();
      assertThat(result, is(notNullValue()));
      assertThat(result, is(hasSize(0)));
    }
  }

}
