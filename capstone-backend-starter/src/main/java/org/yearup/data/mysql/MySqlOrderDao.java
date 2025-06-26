package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order create(Order order) {
        // First, insert the order
        String orderSql = "INSERT INTO orders (user_id, date, shipping_amount) VALUES (?, ?, ?)";

        try (Connection connection = getConnection()) {
            // Start transaction
            connection.setAutoCommit(false);

            try {
                // Insert order
                PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, order.getUserId());
                orderStmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                orderStmt.setBigDecimal(3, order.getTotal());
                orderStmt.executeUpdate();

                // Get generated order ID
                ResultSet keys = orderStmt.getGeneratedKeys();
                if (keys.next()) {
                    int orderId = keys.getInt(1);
                    order.setOrderId(orderId);

                    // Insert line items
                    insertLineItems(connection, orderId, order.getLineItems());
                }

                // Commit transaction
                connection.commit();
                return order;

            } catch (SQLException e) {
                // Rollback on error
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating order", e);
        }
    }

    @Override
    public Order getById(int orderId) {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String lineItemsSql = """
            SELECT oli.*, p.product_id, p.name, p.price as current_price, p.category_id, 
                   p.description, p.color, p.stock, p.image_url, p.featured
            FROM order_line_items oli
            JOIN products p ON oli.product_id = p.product_id
            WHERE oli.order_id = ?
            """;

        try (Connection connection = getConnection()) {
            // Get order
            PreparedStatement orderStmt = connection.prepareStatement(orderSql);
            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (orderRs.next()) {
                Order order = mapOrderRow(orderRs);

                // Get line items
                PreparedStatement lineItemsStmt = connection.prepareStatement(lineItemsSql);
                lineItemsStmt.setInt(1, orderId);
                ResultSet lineItemsRs = lineItemsStmt.executeQuery();

                List<OrderLineItem> lineItems = new ArrayList<>();
                while (lineItemsRs.next()) {
                    OrderLineItem lineItem = mapLineItemRow(lineItemsRs);
                    lineItems.add(lineItem);
                }

                order.setLineItems(lineItems);
                return order;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting order", e);
        }

        return null;
    }

    private void insertLineItems(Connection connection, int orderId, List<OrderLineItem> lineItems) throws SQLException {
        String lineItemSql = "INSERT INTO order_line_items (order_id, product_id, quantity, sales_price, discount) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement lineItemStmt = connection.prepareStatement(lineItemSql);

        for (OrderLineItem lineItem : lineItems) {
            lineItemStmt.setInt(1, orderId);
            lineItemStmt.setInt(2, lineItem.getProductId());
            lineItemStmt.setInt(3, lineItem.getQuantity());
            lineItemStmt.setBigDecimal(4, lineItem.getPrice());
            lineItemStmt.setBigDecimal(5, lineItem.getDiscountPercent());
            lineItemStmt.executeUpdate();
        }
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getTimestamp("date").toLocalDateTime());
        order.setTotal(rs.getBigDecimal("shipping_amount"));
        return order;
    }

    private OrderLineItem mapLineItemRow(ResultSet rs) throws SQLException {
        OrderLineItem lineItem = new OrderLineItem();
        lineItem.setOrderId(rs.getInt("order_id"));
        lineItem.setProductId(rs.getInt("product_id"));
        lineItem.setQuantity(rs.getInt("quantity"));
        lineItem.setPrice(rs.getBigDecimal("sales_price"));
        lineItem.setDiscountPercent(rs.getBigDecimal("discount"));

        // Map product info
        Product product = mapProductFromLineItem(rs);
        lineItem.setProduct(product);

        return lineItem;
    }

    private Product mapProductFromLineItem(ResultSet rs) throws SQLException {
        int productId = rs.getInt("product_id");
        String name = rs.getString("name");
        BigDecimal currentPrice = rs.getBigDecimal("current_price");
        int categoryId = rs.getInt("category_id");
        String description = rs.getString("description");
        String color = rs.getString("color");
        int stock = rs.getInt("stock");
        boolean featured = rs.getBoolean("featured");
        String imageUrl = rs.getString("image_url");

        return new Product(productId, name, currentPrice, categoryId, description, color, stock, featured, imageUrl);
    }
}