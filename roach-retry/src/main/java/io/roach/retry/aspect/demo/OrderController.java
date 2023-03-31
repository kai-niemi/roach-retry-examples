package io.roach.retry.aspect.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/order")
public class OrderController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderService orderService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Order> findOrders() {
        return new ArrayList<>(orderService.findAllOrders());
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Order getOrderById(@PathVariable("id") Long id) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");
        return orderService.getOrderById(id);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<Void> deleteOrders() {
        orderService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/template")
    public OrderRequest getOrderTemplate() {
        Address address1 = new Address();
        address1.setAddress1("Street 1.1");
        address1.setAddress2("Street 1.2");
        address1.setCity("City 1");
        address1.setPostcode("Code 1");
        address1.setCountry("Country 1");

        Address address2 = new Address();
        address2.setAddress1("Street 2.1");
        address2.setAddress2("Street 2.2");
        address2.setCity("City 2");
        address2.setPostcode("Code 2");
        address2.setCountry("Country 2");

        OrderRequest template = new OrderRequest();
        template.setBillAddress(address1);
        template.setDeliveryAddress(address2);
        template.setRequestId(UUID.randomUUID().toString());
        template.setCustomerId(-1L);

        return template;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<Order> submitOrder(OrderRequest request) {
        Order order = new Order();
        order.setBillAddress(request.getBillAddress());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(ShipmentStatus.PLACED);
        order.setDatePlaced(new Date());
        order.setTotalPrice(BigDecimal.TEN);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(order));
    }

    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") ShipmentStatus shipmentStatus,
                                   @RequestParam(value = "amount", defaultValue = "0") BigDecimal amount,
                                   @RequestParam(value = "delay", defaultValue = "0") long commitDelay) {
        logger.info("update order id={} status={} amount={} delay={}", id, shipmentStatus, amount, commitDelay);
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");
        return orderService.updateOrderStatus(id, shipmentStatus, amount, commitDelay);
    }
}