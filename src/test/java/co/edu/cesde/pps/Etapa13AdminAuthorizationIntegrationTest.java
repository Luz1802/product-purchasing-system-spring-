package co.edu.cesde.pps;

import co.edu.cesde.pps.application.CatalogApplicationService;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Etapa13AdminAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    private Role adminRole;
    private Role customerRole;
    private Long categoryId;

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

        adminRole = roleRepository.save(Role.builder()
                .name("ADMIN")
                .description("Administrator user")
                .build());

        customerRole = roleRepository.save(Role.builder()
                .name("CUSTOMER")
                .description("Regular customer user")
                .build());

        orderStatusRepository.save(OrderStatus.builder()
                .name("PENDING")
                .description("Order created, awaiting payment")
                .build());

        categoryId = catalogApplicationService.createCategory(new CategoryUpsertRequest(null, "Admin Categoria", null)).id();
    }

    @Test
    void shouldReturnUnauthorizedWhenAdminEndpointHasNoToken() throws Exception {
        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload("ADM-001")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnUnauthorizedWhenGuestSessionCallsAdminEndpoint() throws Exception {
        MvcResult guestSession = mockMvc.perform(post("/api/v1/auth/guest-session"))
                .andExpect(status().isCreated())
                .andReturn();

        String guestToken = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(guestSession.getResponse().getContentAsString())
                .path("sessionToken")
                .asText();

        mockMvc.perform(post("/api/v1/admin/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(guestToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload("ADM-002")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnForbiddenWhenCustomerCallsAdminEndpoint() throws Exception {
        String customerToken = createAuthenticatedSession(customerRole, "customer@cesde.edu.co");

        mockMvc.perform(post("/api/v1/admin/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload("ADM-003")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldAllowAdminUserToCallAdminEndpoint() throws Exception {
        String adminToken = createAuthenticatedSession(adminRole, "admin@cesde.edu.co");

        mockMvc.perform(post("/api/v1/admin/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productPayload("ADM-004")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("ADM-004"))
                .andExpect(jsonPath("$.image").value(imageForSku("ADM-004")));
    }

    private String createAuthenticatedSession(Role role, String email) {
        User user = userRepository.save(User.builder()
                .role(role)
                .email(email)
                .passwordHash(passwordHasher.hash("secret123"))
                .firstName("Test")
                .lastName(role.getName())
                .phone("3001234567")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        return userSessionService.createAuthenticatedSession(user).getSessionToken();
    }

    private String productPayload(String sku) {
        return """
                {
                  "categoryId": %d,
                  "sku": "%s",
                  "name": "Producto Admin %s",
                  "description": "Producto protegido para admin",
                  "image": "%s",
                  "price": 99.90,
                  "stockQty": 10,
                  "isActive": true
                }
                """.formatted(categoryId, sku, sku, imageForSku(sku));
    }

    private String imageForSku(String sku) {
        return "https://example.com/images/" + sku.toLowerCase() + ".jpg";
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

