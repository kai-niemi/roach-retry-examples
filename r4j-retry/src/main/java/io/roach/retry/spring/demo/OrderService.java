package io.roach.retry.spring.demo;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Service
public class OrderService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<Order> findAllOrders() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        CriteriaQuery<Order> cq = entityManager.getCriteriaBuilder().createQuery(Order.class);
        cq.select(cq.from(Order.class));

        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Order getOrderById(Long orderId) {
        Order order = entityManager.find(Order.class, orderId);
        if (order == null) {
            throw new ObjectRetrievalFailureException(Order.class, orderId);
        }
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order placeOrder(Order order) {
        entityManager.persist(order);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retry(name = "updateOrder")
//    @Retryable(exceptionExpression = "@exceptionClassifier.shouldRetry(#root)",
//            maxAttempts = 5,
//            backoff = @Backoff(maxDelay = 15_000, multiplier = 1.5))
    public Order updateOrderStatus(Long orderId, ShipmentStatus status, BigDecimal amount,
                                   long commitDelay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        Order order = entityManager.find(Order.class, orderId);
        if (order == null) {
            throw new ObjectRetrievalFailureException(Order.class, orderId);
        }

        if (commitDelay > 0) {
            logger.info("Waiting {} ms before write and commit", commitDelay);
            try {
                Thread.sleep(commitDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("Proceeding with write");
        }

        order.setStatus(status);

        if (BigDecimal.ZERO.compareTo(amount) != 0) {
            order.setTotalPrice(order.getTotalPrice().add(amount));
        }

        entityManager.merge(order);
        entityManager.flush();

        if (true) {
            throw  new SQLException("Fake ")
        }

        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");
        Query removeAll = entityManager.createQuery("delete from Order");
        removeAll.executeUpdate();
    }
}
