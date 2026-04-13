package co.edu.cesde.pps.config.demo;

import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.enums.AddressType;
import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.enums.UserStatus;
import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.model.CartItem;
import co.edu.cesde.pps.model.Category;
import co.edu.cesde.pps.model.Order;
import co.edu.cesde.pps.model.OrderItem;
import co.edu.cesde.pps.model.OrderStatus;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.OrderStatusRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.Constants;
import co.edu.cesde.pps.util.MoneyUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import co.edu.cesde.pps.security.PasswordHasher;

@Component
@Profile("demo")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    public static final String ADMIN_EMAIL = "admin.demo@pps.com";
    public static final String ADMIN_PASSWORD = "Admin12345*";
    public static final String CUSTOMER_EMAIL = "customer.demo@pps.com";
    public static final String CUSTOMER_PASSWORD = "Customer12345*";
    public static final String GUEST_SESSION_TOKEN = "demo-guest-session-token";
    public static final String DEMO_ORDER_NUMBER = "ORD-DEMO-0001";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserSessionRepository userSessionRepository;
    private final CartRepository cartRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final PasswordHasher passwordHasher;

    public DemoDataSeeder(RoleRepository roleRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          AddressRepository addressRepository,
                          UserSessionRepository userSessionRepository,
                          CartRepository cartRepository,
                          OrderStatusRepository orderStatusRepository,
                          OrderRepository orderRepository,
                          PasswordHasher passwordHasher) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.userSessionRepository = userSessionRepository;
        this.cartRepository = cartRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderRepository = orderRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = upsertRole("ADMIN", "Demo administrator role");
        Role customerRole = upsertRole("CUSTOMER", "Demo customer role");
        OrderStatus pendingStatus = upsertOrderStatus("PENDING", "Order created, awaiting payment");

        User adminUser = upsertUser(adminRole, ADMIN_EMAIL, ADMIN_PASSWORD, "Admin", "Demo", "3000000001");
        User customerUser = upsertUser(customerRole, CUSTOMER_EMAIL, CUSTOMER_PASSWORD, "Customer", "Demo", "3000000002");

        Category electronics = upsertCategory(null, "Electronics", "electronics");
        Category accessories = upsertCategory(null, "Accessories", "accessories");
        Category computers = upsertCategory(electronics, "Computers", "computers");
        Category audio = upsertCategory(electronics, "Audio", "audio");
        Category keyboards = upsertCategory(accessories, "Keyboards", "keyboards");

        Product laptop = upsertProduct(
                computers,
                "LAP-001",
                "Laptop Demo",
                "Laptop demo estable para integracion",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=1200&q=80",
                MoneyUtils.of("1299.90"),
                12,
                true
        );
        Product headphones = upsertProduct(
                audio,
                "HEAD-001",
                "Audifonos Demo",
                "Audifonos demo estables para integracion",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1200&q=80",
                MoneyUtils.of("249.90"),
                25,
                true
        );
        Product keyboard = upsertProduct(
                keyboards,
                "KEY-001",
                "Teclado Demo",
                "Teclado mecanico demo",
                "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?auto=format&fit=crop&w=1200&q=80",
                MoneyUtils.of("149.90"),
                18,
                true
        );
        upsertProduct(
                accessories,
                "OLD-001",
                "Producto Inactivo Demo",
                "Producto sembrado como inactivo para la UI",
                "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=1200&q=80",
                MoneyUtils.of("59.90"),
                4,
                false
        );

        Address shippingAddress = upsertAddress(customerUser, AddressType.SHIPPING, "Calle Demo 10 #20-30", null,
                "Medellin", "Antioquia", "Colombia", "050001", true);
        Address billingAddress = upsertAddress(customerUser, AddressType.BILLING, "Carrera Demo 15 #40-50", "Oficina 302",
                "Medellin", "Antioquia", "Colombia", "050002", false);

        UserSession guestSession = upsertGuestSession();
        upsertGuestCart(guestSession, headphones);
        upsertDemoOrder(customerUser, pendingStatus, shippingAddress, billingAddress, laptop, keyboard);

        log.info("Demo seed ready: admin={}, customer={}, guestToken={}, orderNumber={}",
                adminUser.getEmail(), customerUser.getEmail(), guestSession.getSessionToken(), DEMO_ORDER_NUMBER);
    }

    private Role upsertRole(String name, String description) {
        Role role = roleRepository.findByNameIgnoreCase(name)
                .orElseGet(Role::new);
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    private OrderStatus upsertOrderStatus(String name, String description) {
        OrderStatus orderStatus = orderStatusRepository.findByNameIgnoreCase(name)
                .orElseGet(OrderStatus::new);
        orderStatus.setName(name);
        orderStatus.setDescription(description);
        return orderStatusRepository.save(orderStatus);
    }

    private User upsertUser(Role role, String email, String rawPassword, String firstName, String lastName, String phone) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(User::new);

        user.setRole(role);
        user.setEmail(email.toLowerCase().trim());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setStatus(UserStatus.ACTIVE);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now().minusDays(7));
        }
        if (user.getPasswordHash() == null || !passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            user.setPasswordHash(passwordHasher.hash(rawPassword));
        }

        return userRepository.save(user);
    }

    private Category upsertCategory(Category parent, String name, String slug) {
        Category category = categoryRepository.findBySlugIgnoreCase(slug)
                .orElseGet(Category::new);
        category.setParent(parent);
        category.setName(name);
        category.setSlug(slug);
        return categoryRepository.save(category);
    }

    private Product upsertProduct(Category category, String sku, String name, String description,
                                  String image, BigDecimal price, Integer stockQty, boolean active) {
        Product product = productRepository.findBySkuIgnoreCase(sku)
                .orElseGet(Product::new);
        product.setCategory(category);
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setImage(image);
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setIsActive(active);
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now().minusDays(5));
        }
        return productRepository.save(product);
    }

    private Address upsertAddress(User user, AddressType type, String line1, String line2,
                                  String city, String state, String country, String postalCode,
                                  boolean isDefault) {
        List<Address> addresses = addressRepository.findByUser_UserId(user.getUserId());
        Address address = addresses.stream()
                .filter(existing -> existing.getType() == type)
                .findFirst()
                .orElseGet(Address::new);

        address.setUser(user);
        address.setType(type);
        address.setLine1(line1);
        address.setLine2(line2);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        address.setIsDefault(isDefault);
        address = addressRepository.save(address);

        if (isDefault) {
            List<Address> updatedAddresses = addressRepository.findByUser_UserId(user.getUserId());
            for (Address other : updatedAddresses) {
                if (!other.getAddressId().equals(address.getAddressId()) && Boolean.TRUE.equals(other.getIsDefault())) {
                    other.setIsDefault(false);
                    addressRepository.save(other);
                }
            }
        }

        return address;
    }

    private UserSession upsertGuestSession() {
        UserSession session = userSessionRepository.findBySessionToken(GUEST_SESSION_TOKEN)
                .orElseGet(UserSession::new);
        session.setUser(null);
        session.setSessionToken(GUEST_SESSION_TOKEN);
        if (session.getCreatedAt() == null) {
            session.setCreatedAt(LocalDateTime.now().minusHours(2));
        }
        session.setExpiresAt(LocalDateTime.now().plusHours(AppConfig.getGuestSessionTimeoutHours()));
        return userSessionRepository.save(session);
    }

    private Cart upsertGuestCart(UserSession guestSession, Product product) {
        List<Cart> carts = cartRepository.findBySession_SessionIdOrderByCreatedAtDesc(guestSession.getSessionId());
        Cart cart = carts.stream().max(Comparator.comparing(Cart::getCreatedAt)).orElseGet(Cart::new);

        cart.setUser(null);
        cart.setSession(guestSession);
        cart.setStatus(CartStatus.OPEN);
        if (cart.getCreatedAt() == null) {
            cart.setCreatedAt(LocalDateTime.now().minusHours(1));
        }
        cart.setUpdatedAt(LocalDateTime.now());
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        cart.getItems().clear();
        cart = cartRepository.saveAndFlush(cart);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .unitPrice(product.getPrice())
                .addedAt(LocalDateTime.now().minusMinutes(45))
                .build();
        cart.getItems().add(cartItem);

        return cartRepository.save(cart);
    }

    private Order upsertDemoOrder(User customerUser, OrderStatus pendingStatus, Address shippingAddress,
                                  Address billingAddress, Product laptop, Product keyboard) {
        Order order = orderRepository.findByOrderNumberIgnoreCase(DEMO_ORDER_NUMBER)
                .orElseGet(Order::new);

        order.setOrderNumber(DEMO_ORDER_NUMBER);
        order.setUser(customerUser);
        order.setOrderStatus(pendingStatus);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now().minusDays(2));
        }
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }
        order.getItems().clear();
        order = orderRepository.saveAndFlush(order);

        OrderItem firstItem = buildOrderItem(order, laptop, 1);
        OrderItem secondItem = buildOrderItem(order, keyboard, 1);
        order.getItems().add(firstItem);
        order.getItems().add(secondItem);

        BigDecimal subtotal = CalculationUtils.calculateOrderSubtotal(
                order.getItems().stream().map(OrderItem::getLineTotal).toList()
        );
        BigDecimal tax = CalculationUtils.calculateTax(subtotal, Constants.DEFAULT_TAX_RATE);
        BigDecimal shipping = subtotal.compareTo(Constants.FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO.setScale(2)
                : Constants.BASE_SHIPPING_COST;

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingCost(shipping);
        order.setTotal(CalculationUtils.calculateOrderTotal(subtotal, tax, shipping));

        return orderRepository.save(order);
    }

    private OrderItem buildOrderItem(Order order, Product product, int quantity) {
        BigDecimal lineTotal = CalculationUtils.calculateOrderItemLineTotal(product.getPrice(), quantity);
        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .lineTotal(lineTotal)
                .build();
    }
}