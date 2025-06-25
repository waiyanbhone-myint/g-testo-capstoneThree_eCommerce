package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.data.mysql.MySqlDaoBase;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;

public interface ShoppingCartDao{

    ShoppingCart getByUserId(int userId);
    // add additional method signatures here

    void addToCart(int userId, int productId);

    void updateQuantity(int userId, int productId, int quantity);

    void clearCart(int userId);
}
