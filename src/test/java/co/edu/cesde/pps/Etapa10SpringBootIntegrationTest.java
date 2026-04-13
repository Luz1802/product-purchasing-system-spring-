package co.edu.cesde.pps;

import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.dto.CategoryDTO;
import co.edu.cesde.pps.dto.OrderDTO;
import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.enums.AddressType;
import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.model.OrderStatus;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.OrderStatusRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.service.AddressService;
import co.edu.cesde.pps.service.CartService;
import co.edu.cesde.pps.service.CategoryService;
import co.edu.cesde.pps.service.OrderService;
import co.edu.cesde.pps.service.ProductService;
import co.edu.cesde.pps.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Etapa10SpringBootIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        addressRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        orderStatusRepository.deleteAll();
        roleRepository.deleteAll();

        roleRepository.save(Role.builder()
                .name("CUSTOMER")
                .description("Regular customer user")
                .build());

        orderStatusRepository.save(OrderStatus.builder()
                .name("PENDING")
                .description("Order created, awaiting payment")
                .build());
    }

    @Test
    void shouldRegisterUserCreateCartAndCheckoutWithJpaRepositories() {
        CategoryDTO category = new CategoryDTO();
        category.setName("Laptops");
        CategoryDTO persistedCategory = categoryService.createCategory(category);

        ProductDTO product = new ProductDTO();
        product.setCategoryId(persistedCategory.getCategoryId());
        product.setSku("LAP-001");
        product.setName("Ultrabook Pro");
        product.setDescription("Notebook de prueba para checkout");
        product.setPrice(new BigDecimal("3500.00"));
        product.setStockQty(8);
        product.setIsActive(true);
        ProductDTO persistedProduct = productService.createProduct(product);

        UserDTO user = userService.registerUser(
                "student@cesde.edu.co",
                "hashed-password",
                "Ada",
                "Lovelace",
                "3001234567"
        );

        AddressDTO shipping = buildAddress(AddressType.SHIPPING, true, "Calle 10 # 20-30");
        AddressDTO billing = buildAddress(AddressType.BILLING, false, "Carrera 15 # 40-50");
        AddressDTO persistedShipping = addressService.addAddress(user.getUserId(), shipping);
        AddressDTO persistedBilling = addressService.addAddress(user.getUserId(), billing);

        CartDTO cart = cartService.createCartForUser(user.getUserId());
        CartDTO cartWithItem = cartService.addItem(cart.getCartId(), persistedProduct.getProductId(), 2);

        OrderDTO order = orderService.checkout(
                user.getUserId(),
                cart.getCartId(),
                persistedShipping.getAddressId(),
                persistedBilling.getAddressId()
        );

        assertThat(cartWithItem.getItems()).hasSize(1);
        assertThat(order.getOrderId()).isNotNull();
        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getUserId()).isEqualTo(user.getUserId());
        assertThat(order.getOrderStatusName()).isEqualTo("PENDING");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getSubtotal()).isEqualByComparingTo("7000.00");
        assertThat(order.getTax()).isEqualByComparingTo("1330.00");
        assertThat(order.getShippingCost()).isEqualByComparingTo("0.00");
        assertThat(order.getTotal()).isEqualByComparingTo("8330.00");
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(cartRepository.findById(cart.getCartId())).get()
                .extracting(Cart::getStatus)
                .isEqualTo(CartStatus.CONVERTED);
        assertThat(productRepository.findById(persistedProduct.getProductId())).get()
                .extracting(Product::getStockQty)
                .isEqualTo(6);
    }

    private AddressDTO buildAddress(AddressType type, boolean isDefault, String line1) {
        AddressDTO dto = new AddressDTO();
        dto.setType(type);
        dto.setLine1(line1);
        dto.setCity("Medellín");
        dto.setState("Antioquia");
        dto.setCountry("Colombia");
        dto.setPostalCode("050001");
        dto.setIsDefault(isDefault);
        return dto;
    }
}


