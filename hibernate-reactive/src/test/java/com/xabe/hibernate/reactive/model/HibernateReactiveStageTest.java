package com.xabe.hibernate.reactive.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

import com.xabe.hibernate.reactive.model.Person.PersonBuilder;
import com.xabe.hibernate.reactive.util.ParallelStreams;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.reactive.stage.Stage.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class HibernateReactiveStageTest {

  private SessionFactory factory;

  private final String person_uuid = UUID.randomUUID().toString();

  final ExecutorService executor = Executors.newFixedThreadPool(5);

  @BeforeAll
  public void setUp() throws Exception {
    final EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
    this.factory = emf.unwrap(SessionFactory.class);
  }

  @Test
  @Order(1)
  public void shouldCreateAccount() throws Exception {
    //Given
    final Account account = Account.builder()
        .name("name")
        .surname("surname")
        .surname2("surname2")
        .personId(this.person_uuid)
        .birthDate(LocalDate.of(1999, 1, 1))
        .amount(BigDecimal.ONE).build();

    //When
    final Account result =
        this.factory.withTransaction(
                (session, tx) -> session.persist(account).thenCompose(__ -> session.flush()).thenApply(__ -> account))
            .toCompletableFuture().join();

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
  }

  @Test
  @Order(2)
  public void shouldCreateCard() throws Exception {
    //Given
    final Card card = Card.builder()
        .name("name")
        .personId(this.person_uuid)
        .expired(LocalDate.of(2999, 1, 1))
        .type(CarType.CREDIT).build();

    //When
    final Card result =
        this.factory.withTransaction((session, tx) -> session.persist(card).thenCompose(__ -> session.flush()).thenApply(__ -> card))
            .toCompletableFuture().join();

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
  }

  @Test
  @Order(3)
  public void shouldCreateLoan() throws Exception {
    //Given
    final Loan loan = Loan.builder()
        .name("name")
        .personId(this.person_uuid)
        .duration(Period.ofYears(6))
        .amount(BigDecimal.TEN)
        .amortizedCapital(BigDecimal.ONE).build();

    //When
    final Loan result =
        this.factory.withTransaction((session, tx) -> session.persist(loan).thenCompose(__ -> session.flush()).thenApply(__ -> loan))
            .toCompletableFuture().join();

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
  }

  @Test
  @Order(4)
  public void shouldGetAllProducts() throws Exception {
    //Given

    //When

    final CompletableFuture<UnaryOperator<PersonBuilder>> accounts =
        this.factory.withSession((session) -> session.createQuery("SELECT a FROM Account a WHERE a.personId = :PersonId", Account.class)
            .setParameter("PersonId", this.person_uuid).getResultList().thenApply(this::createAccountBuilder)).toCompletableFuture();

    final CompletableFuture<UnaryOperator<PersonBuilder>> cards =
        this.factory.withSession((session) -> session.createQuery("SELECT c FROM Card c WHERE c.personId = :PersonId", Card.class)
            .setParameter("PersonId", this.person_uuid).getResultList().thenApply(this::createCardBuilder)).toCompletableFuture();

    final CompletableFuture<UnaryOperator<PersonBuilder>> loans =
        this.factory.withSession((session) -> session.createQuery("SELECT c FROM Loan c WHERE c.personId = :PersonId", Loan.class)
            .setParameter("PersonId", this.person_uuid).getResultList().thenApply(this::createLoanBuilder)).toCompletableFuture();

    final List<CompletableFuture<UnaryOperator<PersonBuilder>>> products = List.of(accounts, cards, loans);

    final List<UnaryOperator<PersonBuilder>> results =
        ParallelStreams.allOfOrException(products, this.executor).join();

    final PersonBuilder builder = Person.builder();
    results.stream().reduce(builder, (p, unary) -> unary.apply(p), (a, b) -> a);
    final Person result = builder.build();

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getAccounts(), is(hasSize(1)));
    assertThat(result.getCards(), is(hasSize(1)));
    assertThat(result.getLoans(), is(hasSize(1)));
  }

  private UnaryOperator<PersonBuilder> createAccountBuilder(final List<Account> accounts) {
    return builder -> {
      builder.accounts(accounts);
      return builder;
    };
  }

  private UnaryOperator<PersonBuilder> createCardBuilder(final List<Card> cards) {
    return builder -> {
      builder.cards(cards);
      return builder;
    };
  }

  private UnaryOperator<PersonBuilder> createLoanBuilder(final List<Loan> loans) {
    return builder -> {
      builder.loans(loans);
      return builder;
    };
  }

}
