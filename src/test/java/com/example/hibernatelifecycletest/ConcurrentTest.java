package com.example.hibernatelifecycletest;

import com.example.hibernatelifecycletest.config.HibernateConfig;
import com.example.hibernatelifecycletest.entity.MyEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class ConcurrentTest {

    @Test
    @DisplayName("Test concurrent updates on the same entity with multiple transactions")
    public void testConcurrentUpdates() {
        // First Transaction: create entity
        doInTransaction(session -> {
            MyEntity entity = new MyEntity();
            entity.setId(1L);
            entity.setName("First Name");
            session.persist(entity);
        });

        // Second Transaction: update and result
        doInTransaction(session -> {
            final MyEntity entity = session.get(MyEntity.class, 1L); // get same entity from first transaction
            try {
                executeSync(() -> doInTransaction(_session -> { // execute other thread
                    MyEntity otherThreadEntity = _session.get(MyEntity.class, 1L);
                    assertNotSame(entity, otherThreadEntity);
                    otherThreadEntity.setName("Second Name");
                }));

                // check the entity of first transaction still has a name "First Name"
                Query<MyEntity> query = session.createQuery("from MyEntity", MyEntity.class);
                MyEntity reloadedEntity = query.uniqueResult();
                assertEquals("First Name", reloadedEntity.getName());

                // HQL을 사용하여 name 필드 값 가져오기
                Query<String> nameQuery = session.createQuery(
                        "select name from MyEntity where id = :id", String.class);
                nameQuery.setParameter("id", entity.getId());
                String updatedName = nameQuery.uniqueResult();

                assertEquals("Second Name", updatedName);

            } catch (InterruptedException e) {
                fail("failed: " + e.getMessage());
            }
        });
    }

    @Test
    public void testConcurrentUpdatesWithJavaUtilConcurrent() {
        doInTransaction(session -> {
            MyEntity entity = new MyEntity();
            entity.setId(1L);
            entity.setName("First Name");
            session.persist(entity);
        });

        doInTransaction(session -> {
            final MyEntity entity = session.get(MyEntity.class, 1L);

            // Using ExecutorService to handle concurrent processing
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<?> future = executor.submit(() -> {
                // Concurrently update MyEntity in a different thread
                doInTransaction(_session -> {
                    MyEntity otherThreadEntity = _session.get(MyEntity.class, 1L);
                    assertNotSame(entity, otherThreadEntity);
                    otherThreadEntity.setName("Second Name");
                });
            });

            try {
                // Wait for the asynchronous task to complete
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Task was interrupted: " + e.getMessage());
            } catch (ExecutionException e) {
                fail("Exception during task execution: " + e.getCause());
            }

            MyEntity reloadedEntity = session.createQuery("from MyEntity", MyEntity.class).uniqueResult();
            assertEquals("First Name", reloadedEntity.getName());

            String updatedName = session.createQuery(
                            "select name from MyEntity where id = :id", String.class)
                    .setParameter("id", entity.getId())
                    .uniqueResult();

            assertEquals("Second Name", updatedName);

            executor.shutdown();
        });
    }


    /**
     * Runs the given action in a separate thread and waits for its completion.
     * <p>
     * This method is used to synchronize the execution of a task in a different thread.
     * It starts the thread, runs the action, and waits for the thread to complete using
     * the {@code join()} method.
     *
     * @param action A {@code Runnable} that defines the task to be executed in the new thread.
     * @throws InterruptedException if the current thread is interrupted while waiting for the
     *                              other thread to complete.
     */
    private void executeSync(Runnable action) throws InterruptedException {
        Thread thread = new Thread(action);
        thread.start();
        thread.join();
    }

    /**
     * Executes the given action within a Hibernate transaction.
     * <p>
     * This method manages the lifecycle of a Hibernate session and transaction.
     * It opens a session, begins a transaction, executes the provided action,
     * and commits the transaction if no exceptions occur. In case of a runtime
     * exception, it rolls back the transaction and rethrows the exception.
     *
     * @param action A {@code Consumer<Session>} that defines the operation to be
     *               executed within the transaction. The action receives the
     *               Hibernate {@code Session} as an argument.
     */
    private void doInTransaction(Consumer<Session> action) {
        Transaction tx = null;

        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.accept(session);   // Executes the action with the session provided
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            throw e;
        }
    }

}
