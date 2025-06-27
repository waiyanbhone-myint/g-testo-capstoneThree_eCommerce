# EasyShop E-Commerce API

A Spring Boot e-commerce REST API with user authentication, shopping cart, and order management.

## 🚀 Features

- **Authentication** - JWT-based user registration and login
- **Product Catalog** - Browse, search, and filter products by category/price/color
- **Shopping Cart** - Add, update, remove items with persistent storage
- **Order Management** - Complete checkout process with order history
- **Profile Management** - User profile CRUD operations
- **Admin Functions** - Category and product management (admin-only)
- **Custom Logging** - Activity tracking with UserActivityLogger
- **Role-Based Security** - User vs Admin access control

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3.x**
- **Spring Security** (JWT authentication)
- **MySQL** database
- **Maven** build tool

## 🚀 Quick Start

1. **Setup Database:**
   ```sql
   CREATE DATABASE easyshop;
   -- Run database/create_database.sql script
   ```

2. **Run Application:**
   ```bash
   mvn spring-boot:run
   ```
   App starts on `http://localhost:8080`

## 📋 API Endpoints

### Authentication
- `POST /register` - User registration
- `POST /login` - User login (returns JWT token)

### Products & Categories
- `GET /products` - Browse all products
- `GET /products?cat=1&color=Black&minPrice=100` - Search/filter
- `GET /categories` - All categories

### Shopping (Requires Auth)
- `GET /cart` - View cart
- `POST /cart/products/{id}` - Add to cart
- `POST /orders` - Checkout
- `GET /profile` - User profile
- `PUT /profile` - Update profile

### Admin Only
- `POST /categories` - Create category
- `POST /products` - Create product

## 💡 Interesting Code Highlight

### Smart Checkout System with Custom Logging
This code shows the complete checkout process - converting cart items to orders with transaction safety and activity tracking:

```java
@PostMapping
public Order checkout(Principal principal) {
    String userName = principal.getName();
    UserActivityLogger.logAction(userName, "Attempting checkout");
    
    // Get user's cart and validate
    ShoppingCart cart = shoppingCartDao.getByUserId(userId);
    if (cart.getItems().isEmpty()) {
        UserActivityLogger.logError(userName, "Checkout failed - empty cart");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot checkout with empty cart");
    }

    // Convert cart items to permanent order line items
    List<OrderLineItem> lineItems = new ArrayList<>();
    for (ShoppingCartItem cartItem : cart.getItems().values()) {
        OrderLineItem lineItem = new OrderLineItem();
        lineItem.setProduct(cartItem.getProduct());
        lineItem.setQuantity(cartItem.getQuantity());
        lineItem.setPrice(cartItem.getProduct().getPrice());
        lineItems.add(lineItem);
    }

    // Create order with calculated total
    Order order = new Order(userId);
    order.setLineItems(lineItems);
    order.calculateTotal();
    
    // Save to database and clear cart (items are now "purchased")
    Order savedOrder = orderDao.create(order);
    shoppingCartDao.clearCart(userId);
    
    UserActivityLogger.logSuccess(userName, "Checkout completed - Order ID: " + savedOrder.getOrderId());
    return savedOrder;
}
```

**This demonstrates:** Business logic implementation, data transformation, transaction management, error handling, and custom logging integration.

## 🧪 Testing

**Use Postman:**
1. Register: `POST /register` with user details
2. Login: `POST /login` → copy JWT token
3. Add Authorization header: `Bearer {token}`
4. Test shopping flow: products → cart → checkout

**Check `user-activity.log` for custom logging output!**

## 📊 Default Users
- **admin** / password (Admin role)
- **user** / password (User role)

## 🎯 Project Phases
- ✅ Phase 1: Categories Controller
- ✅ Phase 2: Product search bug fixes
- ✅ Phase 3: Shopping Cart
- ✅ Phase 4: User Profile + Custom Logging
- ✅ Phase 5: Order/Checkout system

---

**Developer:** Wai Yan Bhone Myint | **Year Up Java Capstone Project**