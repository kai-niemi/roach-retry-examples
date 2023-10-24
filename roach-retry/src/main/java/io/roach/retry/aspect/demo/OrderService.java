package io.roach.retry.aspect.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cockroachdb.annotations.Retryable;
import org.springframework.data.cockroachdb.annotations.TransactionBoundary;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.math.BigDecimal;
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

    @TransactionBoundary
    @Retryable
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

        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");
        Query removeAll = entityManager.createQuery("delete from Order");
        removeAll.executeUpdate();
    }
}
