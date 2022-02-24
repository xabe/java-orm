package com.xabe.jpa.hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "programming_language")
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor()
public class ProgrammingLanguage implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programming_language_sequence")
  @SequenceGenerator(name = "programming_language_sequence", sequenceName = "programming_language_sequence", initialValue = 100)
  @Column(name = "Id")
  private Integer id;

  @Column(name = "Name")
  private String name;

  @Column(name = "Rating")
  private Integer rating;

  @CreationTimestamp
  private LocalDateTime createDateTime;

  @UpdateTimestamp
  private LocalDateTime updateDateTime;

  @Version
  private int version;

  public ProgrammingLanguage(final String name, final Integer rating) {
    this.name = name;
    this.rating = rating;
  }

}
