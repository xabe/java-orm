package com.xabe.hibernate.reactive.model;

import java.math.BigDecimal;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "t_loan", indexes = {
    @Index(name = "person_id_index", columnList = "PersonId", unique = true)
})
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor()
public class Loan {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_sequence")
  @SequenceGenerator(name = "loan_sequence", sequenceName = "loan_sequence", initialValue = 100)
  @Column(name = "Id")
  private String id;

  @Column(name = "PersonId")
  private String personId;

  @Column(name = "Name")
  private String name;

  @Column(name = "Amount")
  private BigDecimal amount;

  @Column(name = "Durantion")
  private Period duration;

  @Column(name = "AmortizedCapital")
  private BigDecimal amortizedCapital;

}
