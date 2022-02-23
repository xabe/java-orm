package com.xabe.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface EventMapper {

  @UpdateProvider(type = EventSqlBuilder.class, method = "dropTableSql")
  void dropTable(@Param("name") String name);

  @UpdateProvider(type = EventSqlBuilder.class, method = "createTableIfNotExistSql")
  void createTableIfNotExist(@Param("name") String name);

  @SelectProvider(type = EventSqlBuilder.class, method = "buildGetNonSentEventsSql")
  List<Event> getNonSentEvents();

  @InsertProvider(type = EventSqlBuilder.class, method = "buildCreateEventSql")
  void create(@Param("event") Event event);

  @UpdateProvider(type = EventSqlBuilder.class, method = "buildMarkEventAsSentSql")
  int markAsSent(@Param("eventId") String eventId, @Param("sentAt") LocalDateTime sentAt);

}
