package com.xabe.hibernate.reactive.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "t_account", indexes = {
    @Index(name = "person_id_index", columnList = "PersonId", unique = true)
})
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor()
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
  @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", initialValue = 100)
  @Column(name = "Id")
  private Integer id;

  @Column(name = "PersonId")
  private String personId;

  @Column(name = "Name")
  private String name;

  @Column(name = "Surname")
  private String surname;

  @Column(name = "Surname2")
  private String surname2;

  @Column(name = "BirthDate")
  private LocalDate birthDate;

  @Column(name = "Amount")
  private BigDecimal amount;

  @CreationTimestamp
  private LocalDateTime createDateTime;

  @UpdateTimestamp
  private LocalDateTime updateDateTime;

  @Version
  private int version;

}
