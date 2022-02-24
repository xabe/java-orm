package com.xabe.hibernate.reactive.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "t_card", indexes = {
    @Index(name = "person_id_index", columnList = "PersonId")
})
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor()
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_sequence")
  @SequenceGenerator(name = "card_sequence", sequenceName = "card_sequence", initialValue = 100)
  @Column(name = "Id")
  private String id;

  @Column(name = "PersonId")
  private String personId;

  @Column(name = "Name")
  private String name;

  @Column(name = "Expired")
  private LocalDate expired;

  @Enumerated(EnumType.STRING)
  @Column(name = "Type")
  private CarType type;

}
