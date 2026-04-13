package co.edu.cesde.pps;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.enums.AddressType;
import co.edu.cesde.pps.enums.UserStatus;
import co.edu.cesde.pps.model.OrderStatus;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.OrderStatusRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.security.PasswordHasher;
import co.edu.cesde.pps.service.UserSessionService;
import co.edu.cesde.pps.web.dto.request.CategoryUpsertRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Etapa12HttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

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

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private UserSessionService userSessionService;

    private String adminToken;

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

        Role adminRole = roleRepository.save(Role.builder()
                .name("ADMIN")
                .description("Administrator user")
                .build());

        roleRepository.save(Role.builder()
                .name("CUSTOMER")
                .description("Regular customer user")
                .build());

        orderStatusRepository.save(OrderStatus.builder()
                .name("PENDING")
                .description("Order created, awaiting payment")
                .build());

        User adminUser = userRepository.save(User.builder()
                .role(adminRole)
                .email("admin@cesde.edu.co")
                .passwordHash(passwordHasher.hash("secret123"))
                .firstName("Admin")
                .lastName("Principal")
                .phone("3001234567")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        adminToken = userSessionService.createAuthenticatedSession(adminUser).getSessionToken();
    }

    @Test
    void shouldExposeAuthEndpointsWithSessionResolution() throws Exception {
        MvcResult guestResult = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionToken").isString())
                .andExpect(jsonPath("$.cart.id").isNumber())
                .andExpect(jsonPath("$.cart.isGuest").value(true))
                .andReturn();

        JsonNode guestBody = objectMapper.readTree(guestResult.getResponse().getContentAsString());
        Long guestCartId = guestBody.path("cart").path("id").asLong();

        String registerPayload = """
                {
                  "email": "ada@cesde.edu.co",
                  "password": "secret123",
                  "firstName": "Ada",
                  "lastName": "Lovelace",
                  "phone": "3001234567",
                  "guestCartId": %d
                }
                """.formatted(guestCartId);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("ada@cesde.edu.co"))
                .andExpect(jsonPath("$.cart.isGuest").value(false))
                .andExpect(jsonPath("$.cart.items.length()").value(0))
                .andReturn();

        JsonNode registerBody = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String sessionToken = registerBody.path("sessionToken").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@cesde.edu.co"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ada@cesde.edu.co",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("ada@cesde.edu.co"))
                .andExpect(jsonPath("$.cart.status").value("OPEN"));
    }

    @Test
    void shouldSupportDocumentedFrontendFlowAcrossControllers() throws Exception {
        Long rootCategoryId = createCategory("Perifericos", null);
        Long childCategoryId = createCategory("Teclados", rootCategoryId);

        MvcResult firstProductResult = createProduct(rootCategoryId, "MOU-001", "Mouse Gamer", 20, true);
        JsonNode firstProductBody = readJson(firstProductResult);
        Long firstProductId = firstProductBody.path("id").asLong();
        assertThat(firstProductBody.path("image").asText()).isEqualTo(imageForSku("MOU-001"));

        MvcResult secondProductResult = createProduct(childCategoryId, "KEY-001", "Teclado Mecanico", 15, true);
        JsonNode secondProductBody = readJson(secondProductResult);
        Long secondProductId = secondProductBody.path("id").asLong();
        assertThat(secondProductBody.path("image").asText()).isEqualTo(imageForSku("KEY-001"));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/v1/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subcategories[0].id").value(childCategoryId));

        mockMvc.perform(get("/api/v1/categories/{id}", rootCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Perifericos"));

        mockMvc.perform(get("/api/v1/categories/{id}/subcategories", rootCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(childCategoryId));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].image").isString());

        mockMvc.perform(get("/api/v1/products")
                        .param("search", "Mouse")
                        .param("categoryId", String.valueOf(rootCategoryId))
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(firstProductId))
                .andExpect(jsonPath("$[0].image").value(imageForSku("MOU-001")));

        mockMvc.perform(get("/api/v1/products/{id}", secondProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado Mecanico"))
                .andExpect(jsonPath("$.image").value(imageForSku("KEY-001")));

        MvcResult guestResult = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode guestBody = readJson(guestResult);
        String guestToken = guestBody.path("sessionToken").asText();
        Long guestCartId = guestBody.path("cart").path("id").asLong();

        mockMvc.perform(get("/api/v1/cart/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(guestToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guestCartId))
                .andExpect(jsonPath("$.isGuest").value(true));

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(guestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": 1
                                }
                                """.formatted(firstProductId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.itemsCount").value(1))
                .andExpect(jsonPath("$.items[0].image").value(imageForSku("MOU-001")));

        MvcResult updatedGuestCartResult = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/cart/items/{productId}", firstProductId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(guestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.itemsCount").value(2))
                .andExpect(jsonPath("$.items[0].image").value(imageForSku("MOU-001")))
                .andReturn();

        Long updatedGuestCartId = readJson(updatedGuestCartResult).path("id").asLong();

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ada@cesde.edu.co",
                                  "password": "secret123",
                                  "firstName": "Ada",
                                  "lastName": "Lovelace",
                                  "phone": "3001234567",
                                  "guestCartId": %d
                                }
                                """.formatted(updatedGuestCartId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cart.isGuest").value(false))
                .andExpect(jsonPath("$.cart.summary.itemsCount").value(2))
                .andExpect(jsonPath("$.cart.items[0].image").value(imageForSku("MOU-001")))
                .andReturn();

        JsonNode registerBody = readJson(registerResult);
        String authenticatedToken = registerBody.path("sessionToken").asText();
        Long authenticatedCartId = registerBody.path("cart").path("id").asLong();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@cesde.edu.co"));

        MvcResult shippingResult = mockMvc.perform(post("/api/v1/users/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressPayload(AddressType.SHIPPING, "Calle 10 #20-30", true)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("SHIPPING"))
                .andReturn();
        Long shippingAddressId = readJson(shippingResult).path("id").asLong();

        MvcResult billingResult = mockMvc.perform(post("/api/v1/users/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressPayload(AddressType.BILLING, "Carrera 15 #40-50", false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("BILLING"))
                .andReturn();
        Long billingAddressId = readJson(billingResult).path("id").asLong();

        MvcResult extraAddressResult = mockMvc.perform(post("/api/v1/users/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressPayload(AddressType.SHIPPING, "Circular 73 #38-11", false)))
                .andExpect(status().isCreated())
                .andReturn();
        Long extraAddressId = readJson(extraAddressResult).path("id").asLong();

        mockMvc.perform(get("/api/v1/users/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        mockMvc.perform(get("/api/v1/users/me/addresses/{id}", shippingAddressId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Medellin"));

        mockMvc.perform(put("/api/v1/users/me/addresses/{id}", billingAddressId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressPayload(AddressType.BILLING, "Carrera 20 #50-10", false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.line1").value("Carrera 20 #50-10"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/users/me/addresses/{id}/default", billingAddressId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));

        mockMvc.perform(delete("/api/v1/users/me/addresses/{id}", extraAddressId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isNoContent());

        MvcResult secondGuestResult = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andReturn();
        String secondGuestToken = readJson(secondGuestResult).path("sessionToken").asText();
        Long secondGuestCartId = readJson(secondGuestResult).path("cart").path("id").asLong();

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(secondGuestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": 1
                                }
                                """.formatted(secondProductId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.itemsCount").value(1))
                .andExpect(jsonPath("$.items[0].image").value(imageForSku("KEY-001")));

        mockMvc.perform(post("/api/v1/cart/merge")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "guestCartId": %d
                                }
                                """.formatted(secondGuestCartId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.summary.itemsCount").value(3))
                .andExpect(jsonPath("$.items[0].image").isString())
                .andExpect(jsonPath("$.items[1].image").isString());

        mockMvc.perform(delete("/api/v1/cart/items/{productId}", secondProductId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].image").isString());

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": 1
                                }
                                """.formatted(secondProductId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].image").isString())
                .andExpect(jsonPath("$.items[1].image").isString());

        MvcResult disposableGuestResult = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andReturn();
        String disposableGuestToken = readJson(disposableGuestResult).path("sessionToken").asText();

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(disposableGuestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": 1
                                }
                                """.formatted(firstProductId)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(disposableGuestToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/cart/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(disposableGuestToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.itemsCount").value(0));

        MvcResult checkoutResult = mockMvc.perform(post("/api/v1/orders/checkout")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cartId": %d,
                                  "shippingAddressId": %d,
                                  "billingAddressId": %d
                                }
                                """.formatted(authenticatedCartId, shippingAddressId, billingAddressId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].image").isString())
                .andExpect(jsonPath("$.items[1].image").isString())
                .andReturn();

        Long orderId = readJson(checkoutResult).path("id").asLong();

        mockMvc.perform(get("/api/v1/orders/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId))
                .andExpect(jsonPath("$[0].items[0].image").isString());

        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(authenticatedToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.shippingAddress.id").value(shippingAddressId))
                .andExpect(jsonPath("$.items[0].image").isString());

        String updatedImage = "https://example.com/images/key-001-rgb.jpg";
        MvcResult updatedProductResult = mockMvc.perform(put("/api/v1/admin/products/{id}", secondProductId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload(childCategoryId, "KEY-001", "Teclado Mecanico RGB", updatedImage, 10, true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado Mecanico RGB"))
                .andExpect(jsonPath("$.image").value(updatedImage))
                .andReturn();

        Long updatedProductId = readJson(updatedProductResult).path("id").asLong();
        assertThat(updatedProductId).isEqualTo(secondProductId);

        MvcResult disposableProductResult = createProduct(rootCategoryId, "PAD-001", "Mouse Pad", 5, true);
        Long disposableProductId = readJson(disposableProductResult).path("id").asLong();

        mockMvc.perform(delete("/api/v1/admin/products/{id}", disposableProductId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", disposableProductId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void shouldReturnStandardizedHttpErrorsForValidationAndBusinessFailures() throws Exception {
        Long categoryId = createCategory("Accesorios", null);
        MvcResult productResult = createProduct(categoryId, "STK-001", "Producto Stock", 1, true);
        Long productId = readJson(productResult).path("id").asLong();

        MvcResult validationResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "123",
                                  "firstName": "",
                                  "lastName": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andReturn();

        JsonNode validationBody = objectMapper.readTree(validationResult.getResponse().getContentAsString());
        Set<String> invalidFields = new HashSet<>();
        validationBody.path("details").forEach(detail -> invalidFields.add(detail.path("field").asText()));
        assertThat(invalidFields).contains("email", "password", "firstName", "lastName");

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/me"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer missing-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Basic abc123"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(get("/api/v1/products/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));

        mockMvc.perform(post("/api/v1/admin/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload(categoryId, "STK-001", "Sku Duplicado", 2, true)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));

        MvcResult guestResult = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andReturn();
        String guestToken = readJson(guestResult).path("sessionToken").asText();

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, bearer(guestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": 99
                                }
                                """.formatted(productId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }

    private Long createCategory(String name, Long parentId) {
        return catalogApplicationService.createCategory(new CategoryUpsertRequest(parentId, name, null)).id();
    }

    private MvcResult createProduct(Long categoryId, String sku, String name, int stockQty, boolean isActive) throws Exception {
        return mockMvc.perform(post("/api/v1/admin/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload(categoryId, sku, name, imageForSku(sku), stockQty, isActive)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String productPayload(Long categoryId, String sku, String name, int stockQty, boolean isActive) {
        return productPayload(categoryId, sku, name, imageForSku(sku), stockQty, isActive);
    }

    private String productPayload(Long categoryId, String sku, String name, String image, int stockQty, boolean isActive) {
        return """
                {
                  "categoryId": %d,
                  "sku": "%s",
                  "name": "%s",
                  "description": "%s descripcion de prueba",
                  "image": "%s",
                  "price": %s,
                  "stockQty": %d,
                  "isActive": %s
                }
                """.formatted(categoryId, sku, name, name, image, new BigDecimal("89.90"), stockQty, isActive);
    }

    private String imageForSku(String sku) {
        return "https://example.com/images/" + sku.toLowerCase() + ".jpg";
    }

    private String addressPayload(AddressType type, String line1, boolean isDefault) {
        return """
                {
                  "type": "%s",
                  "line1": "%s",
                  "line2": null,
                  "city": "Medellin",
                  "state": "Antioquia",
                  "country": "Colombia",
                  "postalCode": "050001",
                  "isDefault": %s
                }
                """.formatted(type.name(), line1, isDefault);
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}


