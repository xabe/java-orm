package com.xabe.jpa.hibernate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class JPAHibernateTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JPAHibernateTest.class);

  //Only One
  private EntityManagerFactory entityManagerFactory;

  private ProgrammingLanguage programmingLanguage;

  @BeforeAll
  public void setUp() throws Exception {
    this.entityManagerFactory = Persistence.createEntityManagerFactory("jpa-demo-local");
    this.programmingLanguage = new ProgrammingLanguage("Java", 10);
  }

  @Test
  @Order(0)
  public void shouldCreateProgrammingLanguage() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    final EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();

    entityManager.persist(this.programmingLanguage);

    transaction.commit();
    entityManager.close();
  }

  @Test
  @Order(1)
  public void shouldGetProgrammingLanguageWithFind() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    final ProgrammingLanguage result = entityManager.find(ProgrammingLanguage.class, this.programmingLanguage.getId());

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId(), is(greaterThanOrEqualTo(100)));
    assertThat(result.getName(), is("Java"));
    assertThat(result.getRating(), is(10));
    assertThat(result.getVersion(), is(greaterThanOrEqualTo(0)));
    entityManager.close();
  }

  @Test
  @Order(2)
  public void shouldGetProgrammingLanguageWithReference() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    final ProgrammingLanguage result = entityManager.getReference(ProgrammingLanguage.class, this.programmingLanguage.getId());

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId(), is(greaterThanOrEqualTo(100)));
    assertThat(result.getName(), is("Java"));
    assertThat(result.getRating(), is(10));
    assertThat(result.getVersion(), is(greaterThanOrEqualTo(0)));
    entityManager.close();
  }

  @Test
  @Order(3)
  public void notShouldGetProgrammingLanguageWithFind() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    final ProgrammingLanguage result = entityManager.find(ProgrammingLanguage.class, 2);

    assertThat(result, is(nullValue()));
    entityManager.close();
  }

  @Test
  @Order(4)
  public void notShouldGetProgrammingLanguageWithReference() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    final ProgrammingLanguage result = entityManager.getReference(ProgrammingLanguage.class, 2);

    assertThat(result, is(notNullValue()));
    Assertions.assertThrows(EntityNotFoundException.class, result::getName);
    entityManager.close();
  }

  @Test
  @Order(5)
  public void shouldUpdateProgrammingLanguage() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    this.programmingLanguage.setRating(100);
    final EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();

    entityManager.merge(this.programmingLanguage);
    transaction.commit();
    this.programmingLanguage = entityManager.find(ProgrammingLanguage.class, this.programmingLanguage.getId());

    assertThat(this.programmingLanguage, is(notNullValue()));
    assertThat(this.programmingLanguage.getId(), is(notNullValue()));
    assertThat(this.programmingLanguage.getId(), is(greaterThanOrEqualTo(100)));
    assertThat(this.programmingLanguage.getName(), is("Java"));
    assertThat(this.programmingLanguage.getRating(), is(100));
    assertThat(this.programmingLanguage.getVersion(), is(greaterThanOrEqualTo(0)));
    entityManager.close();
  }

  @Test
  @Order(6)
  public void shouldDeleteProgrammingLanguage() throws Exception {
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    final EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();

    entityManager.remove(
        entityManager.contains(this.programmingLanguage) ? this.programmingLanguage : entityManager.merge(this.programmingLanguage));

    transaction.commit();
    final ProgrammingLanguage result = entityManager.find(ProgrammingLanguage.class, this.programmingLanguage.getId());
    assertThat(result, is(nullValue()));
    entityManager.close();
  }

  @Test
  @Order(7)
  public void shouldExceptionOptimisticLock() throws InterruptedException {
    final ProgrammingLanguage programmingLanguage = new ProgrammingLanguage("JavaScript", 10);
    final EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    final EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();
    entityManager.persist(programmingLanguage);
    transaction.commit();

    final ProgrammingLanguage partialResult = entityManager.find(ProgrammingLanguage.class, programmingLanguage.getId());

    assertThat(partialResult, is(notNullValue()));
    assertThat(partialResult.getId(), is(greaterThanOrEqualTo(100)));
    assertThat(partialResult.getVersion(), is(greaterThanOrEqualTo(0)));
    entityManager.close();
    LOGGER.info("Insert {}", partialResult);

    final int threadsNumber = 10;
    final AtomicInteger atomicInteger = new AtomicInteger();
    final CountDownLatch startLatch = new CountDownLatch(threadsNumber + 1);
    final CountDownLatch endLatch = new CountDownLatch(threadsNumber + 1);

    for (; atomicInteger.get() < threadsNumber; atomicInteger.incrementAndGet()) {
      final long index = (long) atomicInteger.get() * threadsNumber;
      LOGGER.info("Scheduling thread index {}", index);
      final Thread testThread = new Thread(() -> {
        try {
          startLatch.countDown();
          startLatch.await();
          final EntityManager entityManagerUpdate = this.entityManagerFactory.createEntityManager();
          final EntityTransaction entityTransaction = entityManagerUpdate.getTransaction();
          try {
            entityTransaction.begin();
            final ProgrammingLanguage updatePartialResult =
                entityManagerUpdate.find(ProgrammingLanguage.class, programmingLanguage.getId());
            updatePartialResult.setRating(100);
            entityManagerUpdate.merge(updatePartialResult);
            entityTransaction.commit();
            LOGGER.info("Update {} index {}", updatePartialResult, index);
          } catch (final Exception e) {
            LOGGER.error("Exception thrown!", e);
            entityTransaction.rollback();
          }
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (final Exception e) {
          LOGGER.error("Exception thrown!", e);
        } finally {
          endLatch.countDown();
        }
      });
      testThread.start();
    }
    startLatch.countDown();
    LOGGER.info("Waiting for threads to be done");
    endLatch.countDown();
    endLatch.await();
    LOGGER.info("Threads are done");

    final EntityManager entityManagerResult = this.entityManagerFactory.createEntityManager();

    final ProgrammingLanguage result = entityManagerResult.find(ProgrammingLanguage.class, partialResult.getId());

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId(), is(greaterThanOrEqualTo(100)));
    assertThat(result.getName(), is("JavaScript"));
    assertThat(result.getRating(), is(100));
    assertThat(result.getVersion(), is(greaterThan(0)));
    entityManager.close();
  }

}
