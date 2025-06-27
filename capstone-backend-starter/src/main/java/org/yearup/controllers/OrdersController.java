package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.UserActivityLogger;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrdersController {

    private OrderDao orderDao;
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;

    @Autowired
    public OrdersController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    public Order checkout(Principal principal) {
        UserActivityLogger.logAction(principal.getName(), "Attempting checkout");
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            if (cart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot checkout with empty cart");
            }

            Order order = new Order(userId);
            order.setOrderDate(LocalDateTime.now());

            List<OrderLineItem> lineItems = new ArrayList<>();

            for (ShoppingCartItem cartItem : cart.getItems().values()) {
                OrderLineItem lineItem = new OrderLineItem();
                lineItem.setProduct(cartItem.getProduct());
                lineItem.setQuantity(cartItem.getQuantity());
                lineItem.setPrice(cartItem.getProduct().getPrice());
                lineItem.setDiscountPercent(cartItem.getDiscountPercent());

                lineItems.add(lineItem);
            }

            order.setLineItems(lineItems);
            order.calculateTotal();

            Order savedOrder = orderDao.create(order);

            shoppingCartDao.clearCart(userId);

            return savedOrder;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Checkout failed");
        }
    }

    @GetMapping("/{orderId}")
    public Order getOrder(Principal principal, @PathVariable int orderId) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Order order = orderDao.getById(orderId);

            if (order == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }

            if (order.getUserId() != userId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            return order;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving order");
        }
    }
}