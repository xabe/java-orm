package com.xabe.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Event {

  @Builder.Default
  public String id = UUID.randomUUID().toString();

  public LocalDateTime createdAt;

  public LocalDateTime sentAt;

  public String name;

  public String body;
}
