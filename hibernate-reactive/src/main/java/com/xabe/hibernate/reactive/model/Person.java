package com.xabe.hibernate.reactive.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor()
public class Person {

  private final String id;

  private final List<Account> accounts;

  private final List<Card> cards;

  private final List<Loan> loans;

}
