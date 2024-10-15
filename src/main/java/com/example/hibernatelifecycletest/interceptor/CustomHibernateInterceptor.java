package com.example.hibernatelifecycletest.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import static org.hibernate.resource.transaction.spi.TransactionStatus.COMMITTED;
import static org.hibernate.resource.transaction.spi.TransactionStatus.ROLLED_BACK;

@Slf4j
@Component
public class CustomHibernateInterceptor implements Interceptor {
    @Override
    public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        log.info("Entity Deleted: " + entity);
        return Interceptor.super.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        log.info("Entity Updated: " + entity);
        return Interceptor.super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        log.info("Entity Saved: " + entity);
        return Interceptor.super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        Interceptor.super.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        // check Transaction status
        if (tx.getStatus().isOneOf(COMMITTED)) {
            log.info("Transaction Completed: Success");
        } else if (tx.getStatus().isOneOf(ROLLED_BACK)) {
            log.info("Transaction Completed: Rolled Back");
        } else {
            log.info("Transaction Completed: Status = " + tx.getStatus());
        }
        Interceptor.super.afterTransactionCompletion(tx);
    }
}
