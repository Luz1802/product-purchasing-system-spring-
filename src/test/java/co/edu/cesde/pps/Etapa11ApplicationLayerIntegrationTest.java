package co.edu.cesde.pps;

import co.edu.cesde.pps.application.AddressApplicationService;
import co.edu.cesde.pps.application.AuthApplicationService;
import co.edu.cesde.pps.application.CartApplicationService;
import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.application.OrderApplicationService;
import co.edu.cesde.pps.exception.AuthenticationException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.model.OrderStatus;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.OrderStatusRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.web.dto.error.ApiErrorCode;
import co.edu.cesde.pps.web.dto.error.ApiErrorResponse;
import co.edu.cesde.pps.web.dto.request.AddCartItemRequest;
import co.edu.cesde.pps.web.dto.request.AddressUpsertRequest;
import co.edu.cesde.pps.web.dto.request.CategoryUpsertRequest;
import co.edu.cesde.pps.web.dto.request.CheckoutRequest;
import co.edu.cesde.pps.web.dto.request.LoginRequest;
import co.edu.cesde.pps.web.dto.request.MergeGuestCartRequest;
import co.edu.cesde.pps.web.dto.request.ProductUpsertRequest;
import co.edu.cesde.pps.web.dto.request.RegisterRequest;
import co.edu.cesde.pps.web.dto.request.UpdateCartItemQuantityRequest;
import co.edu.cesde.pps.web.dto.response.AddressResponse;
import co.edu.cesde.pps.web.dto.response.AuthSessionResponse;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import co.edu.cesde.pps.web.error.ErrorResponseFactory;
import co.edu.cesde.pps.enums.AddressType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Etapa11ApplicationLayerIntegrationTest {

    private static final String MOUSE_IMAGE = "https://example.com/images/mou-001.jpg";
    private static final String KEYBOARD_IMAGE = "https://example.com/images/key-001.jpg";

    @Autowired
    private AuthApplicationService authApplicationService;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @Autowired
    private CartApplicationService cartApplicationService;

    @Autowired
    private AddressApplicationService addressApplicationService;

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private ErrorResponseFactory errorResponseFactory;

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
    private UserSessionRepository userSessionRepository;

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
        userSessionRepository.deleteAll();
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
    void shouldPrepareApplicationLayerForEtapa12WithoutControllers() {
        var category = catalogApplicationService.createCategory(new CategoryUpsertRequest(
                null,
                "Perifericos",
                null
        ));

        ProductResponse firstProduct = catalogApplicationService.createProduct(new ProductUpsertRequest(
                category.id(),
                "MOU-001",
                "Mouse Gamer",
                "Mouse para pruebas de carrito guest",
                MOUSE_IMAGE,
                new BigDecimal("89.90"),
                20,
                true
        ));

        ProductResponse secondProduct = catalogApplicationService.createProduct(new ProductUpsertRequest(
                category.id(),
                "KEY-001",
                "Teclado Mecanico",
                "Segundo producto para validar merge",
                KEYBOARD_IMAGE,
                new BigDecimal("120.00"),
                15,
                true
        ));

        List<ProductResponse> activeProducts = catalogApplicationService.listProducts(true);
        assertThat(activeProducts).hasSize(2);
        assertThat(firstProduct.image()).isEqualTo(MOUSE_IMAGE);
        assertThat(secondProduct.image()).isEqualTo(KEYBOARD_IMAGE);
        assertThat(activeProducts).extracting(ProductResponse::image)
                .containsExactlyInAnyOrder(MOUSE_IMAGE, KEYBOARD_IMAGE);
        assertThat(catalogApplicationService.listCategoryTree()).hasSize(1);

        AuthSessionResponse guestSession = authApplicationService.createGuestSession();
        assertThat(guestSession.user()).isNull();
        assertThat(guestSession.cart()).isNotNull();
        assertThat(guestSession.cart().isGuest()).isTrue();

        CartResponse guestCart = cartApplicationService.addItem(
                guestSession.sessionToken(),
                new AddCartItemRequest(firstProduct.id(), 1)
        );
        guestCart = cartApplicationService.updateItemQuantity(
                guestSession.sessionToken(),
                firstProduct.id(),
                new UpdateCartItemQuantityRequest(2)
        );
        assertThat(guestCart.summary().itemsCount()).isEqualTo(2);
        assertThat(guestCart.items()).hasSize(1);
        assertThat(guestCart.items().get(0).image()).isEqualTo(MOUSE_IMAGE);

        AuthSessionResponse registeredSession = authApplicationService.register(new RegisterRequest(
                "ada@cesde.edu.co",
                "secret123",
                "Ada",
                "Lovelace",
                "3001234567",
                guestCart.id()
        ));

        assertThat(registeredSession.user()).isNotNull();
        assertThat(registeredSession.cart().isGuest()).isFalse();
        assertThat(registeredSession.cart().summary().itemsCount()).isEqualTo(2);
        assertThat(registeredSession.cart().items().get(0).image()).isEqualTo(MOUSE_IMAGE);
        assertThat(authApplicationService.getCurrentUser(registeredSession.sessionToken()).email())
                .isEqualTo("ada@cesde.edu.co");

        AddressResponse shipping = addressApplicationService.addAddress(
                registeredSession.sessionToken(),
                new AddressUpsertRequest(AddressType.SHIPPING, "Calle 10 #20-30", null,
                        "Medellin", "Antioquia", "Colombia", "050001", true)
        );
        AddressResponse billing = addressApplicationService.addAddress(
                registeredSession.sessionToken(),
                new AddressUpsertRequest(AddressType.BILLING, "Carrera 15 #40-50", null,
                        "Medellin", "Antioquia", "Colombia", "050001", false)
        );

        assertThat(addressApplicationService.listMyAddresses(registeredSession.sessionToken())).hasSize(2);
        assertThat(addressApplicationService.getMyAddress(registeredSession.sessionToken(), shipping.id()).city())
                .isEqualTo("Medellin");

        AuthSessionResponse anotherGuestSession = authApplicationService.createGuestSession();
        CartResponse anotherGuestCart = cartApplicationService.addItem(
                anotherGuestSession.sessionToken(),
                new AddCartItemRequest(secondProduct.id(), 1)
        );

        CartResponse mergedCart = cartApplicationService.mergeGuestCart(
                registeredSession.sessionToken(),
                new MergeGuestCartRequest(anotherGuestCart.id())
        );
        assertThat(mergedCart.items()).hasSize(2);
        assertThat(mergedCart.summary().itemsCount()).isEqualTo(3);
        assertThat(mergedCart.items())
                .extracting(item -> item.image())
                .containsExactlyInAnyOrder(MOUSE_IMAGE, KEYBOARD_IMAGE);

        OrderResponse order = orderApplicationService.checkout(
                registeredSession.sessionToken(),
                new CheckoutRequest(mergedCart.id(), shipping.id(), billing.id())
        );

        assertThat(order.id()).isNotNull();
        assertThat(order.orderNumber()).startsWith("ORD-");
        assertThat(order.items()).hasSize(2);
        assertThat(order.items())
                .extracting(item -> item.image())
                .containsExactlyInAnyOrder(MOUSE_IMAGE, KEYBOARD_IMAGE);
        assertThat(order.shippingAddress()).isNotNull();
        assertThat(order.billingAddress()).isNotNull();
        assertThat(orderApplicationService.listMyOrders(registeredSession.sessionToken())).hasSize(1);
        assertThat(orderApplicationService.getMyOrder(registeredSession.sessionToken(), order.id()).id())
                .isEqualTo(order.id());

        authApplicationService.logout(registeredSession.sessionToken());

        AuthSessionResponse loginSession = authApplicationService.login(new LoginRequest(
                "ada@cesde.edu.co",
                "secret123",
                null
        ));
        assertThat(loginSession.user()).isNotNull();
        assertThat(loginSession.cart()).isNotNull();
        assertThat(loginSession.cart().status()).isEqualTo("OPEN");
    }

    @Test
    void shouldBuildNormalizedApiErrorsForFutureAdvice() {
        ApiErrorResponse validationError = errorResponseFactory.fromException(
                new ValidationException("quantity", 0, "must be positive"),
                "/api/v1/cart/items"
        );
        ApiErrorResponse authenticationError = errorResponseFactory.fromException(
                new AuthenticationException("Invalid credentials"),
                "/api/v1/auth/login"
        );

        assertThat(validationError.code()).isEqualTo(ApiErrorCode.VALIDATION_ERROR);
        assertThat(validationError.path()).isEqualTo("/api/v1/cart/items");
        assertThat(authenticationError.code()).isEqualTo(ApiErrorCode.UNAUTHORIZED);
        assertThat(authenticationError.message()).isEqualTo("Invalid credentials");
    }
}

