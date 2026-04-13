package co.edu.cesde.pps;

import co.edu.cesde.pps.config.demo.DemoDataSeeder;
import co.edu.cesde.pps.enums.AddressType;
import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.model.Order;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.security.PasswordHasher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
class DemoProfileSeedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DemoDataSeeder demoDataSeeder;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void reseedDemoData() throws Exception {
        demoDataSeeder.run(new DefaultApplicationArguments(new String[0]));
    }

    @Test
    void shouldSeedStableDemoDatasetIdempotently() throws Exception {
        demoDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN").orElseThrow();
        Role customerRole = roleRepository.findByNameIgnoreCase("CUSTOMER").orElseThrow();
        assertThat(adminRole.getName()).isEqualTo("ADMIN");
        assertThat(customerRole.getName()).isEqualTo("CUSTOMER");

        User adminUser = userRepository.findByEmailIgnoreCase(DemoDataSeeder.ADMIN_EMAIL).orElseThrow();
        User customerUser = userRepository.findByEmailIgnoreCase(DemoDataSeeder.CUSTOMER_EMAIL).orElseThrow();
        assertThat(passwordHasher.matches(DemoDataSeeder.ADMIN_PASSWORD, adminUser.getPasswordHash())).isTrue();
        assertThat(passwordHasher.matches(DemoDataSeeder.CUSTOMER_PASSWORD, customerUser.getPasswordHash())).isTrue();

        assertThat(categoryRepository.findBySlugIgnoreCase("electronics")).isPresent();
        assertThat(categoryRepository.findBySlugIgnoreCase("accessories")).isPresent();
        assertThat(categoryRepository.findBySlugIgnoreCase("computers")).isPresent();

        Product laptop = productRepository.findBySkuIgnoreCase("LAP-001").orElseThrow();
        assertThat(laptop.getImage()).isNotBlank();

        Product inactiveProduct = productRepository.findBySkuIgnoreCase("OLD-001").orElseThrow();
        assertThat(inactiveProduct.getIsActive()).isFalse();
        assertThat(inactiveProduct.getImage()).isNotBlank();

        List<Address> addresses = addressRepository.findByUser_UserId(customerUser.getUserId());
        assertThat(addresses).hasSize(2);
        assertThat(addresses.stream().filter(address -> Boolean.TRUE.equals(address.getIsDefault())).count()).isEqualTo(1);

        UserSession guestSession = userSessionRepository.findBySessionToken(DemoDataSeeder.GUEST_SESSION_TOKEN).orElseThrow();
        assertThat(guestSession.getUser()).isNull();

        List<Cart> guestCarts = cartRepository.findBySession_SessionIdOrderByCreatedAtDesc(guestSession.getSessionId());
        assertThat(guestCarts).hasSize(1);
        assertThat(guestCarts.get(0).isGuestCart()).isTrue();
        assertThat(guestCarts.get(0).isOpen()).isTrue();

        mockMvc.perform(get("/api/v1/cart/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(DemoDataSeeder.GUEST_SESSION_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].image").isString());

        Order demoOrder = orderRepository.findByOrderNumberIgnoreCase(DemoDataSeeder.DEMO_ORDER_NUMBER).orElseThrow();
        assertThat(demoOrder.getUser().getUserId()).isEqualTo(customerUser.getUserId());
        assertThat(demoOrder.getTotal()).isPositive();
    }

    @Test
    void shouldExposeDocumentedContractsUsingDemoData() throws Exception {
        String customerToken = login(DemoDataSeeder.CUSTOMER_EMAIL, DemoDataSeeder.CUSTOMER_PASSWORD)
                .path("sessionToken")
                .asText();
        String adminToken = login(DemoDataSeeder.ADMIN_EMAIL, DemoDataSeeder.ADMIN_PASSWORD)
                .path("sessionToken")
                .asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        MvcResult treeResult = mockMvc.perform(get("/api/v1/categories/tree"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode treeBody = objectMapper.readTree(treeResult.getResponse().getContentAsString());
        JsonNode electronics = findNodeBySlug(treeBody, "electronics");
        assertThat(electronics).isNotNull();
        assertThat(electronics.path("subcategories").isArray()).isTrue();
        assertThat(findNodeBySlug(electronics.path("subcategories"), "computers")).isNotNull();

        Product laptop = productRepository.findBySkuIgnoreCase("LAP-001").orElseThrow();

        mockMvc.perform(get("/api/v1/cart/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(DemoDataSeeder.GUEST_SESSION_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].image").isString());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].image").isString());

        mockMvc.perform(get("/api/v1/products/{id}", laptop.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("LAP-001"))
                .andExpect(jsonPath("$.image").value(laptop.getImage()));

        mockMvc.perform(get("/api/v1/orders/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/v1/orders/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value(DemoDataSeeder.DEMO_ORDER_NUMBER))
                .andExpect(jsonPath("$[0].items[0].image").isString());

        User customerUser = userRepository.findByEmailIgnoreCase(DemoDataSeeder.CUSTOMER_EMAIL).orElseThrow();
        Address billingAddress = addressRepository.findByUser_UserId(customerUser.getUserId()).stream()
                .filter(address -> address.getType() == AddressType.BILLING)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/api/v1/users/me/addresses/{id}/default", billingAddress.getAddressId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(billingAddress.getAddressId()))
                .andExpect(jsonPath("$.type").value("BILLING"))
                .andExpect(jsonPath("$.isDefault").value(true));

        mockMvc.perform(post("/api/v1/cart/merge")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "guestCartId": 999999
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));

        UserSession guestSession = userSessionRepository.findBySessionToken(DemoDataSeeder.GUEST_SESSION_TOKEN).orElseThrow();
        Cart guestCart = cartRepository.findBySession_SessionIdOrderByCreatedAtDesc(guestSession.getSessionId()).get(0);

        mockMvc.perform(post("/api/v1/cart/merge")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "guestCartId": %d
                                }
                                """.formatted(guestCart.getCartId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isGuest").value(false));

        mockMvc.perform(post("/api/v1/cart/merge")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "guestCartId": %d
                                }
                                """.formatted(guestCart.getCartId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_CART_STATE"));
    }

    private JsonNode login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode findNodeBySlug(JsonNode arrayNode, String slug) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return null;
        }
        for (JsonNode node : arrayNode) {
            if (slug.equals(node.path("slug").asText())) {
                return node;
            }
        }
        return null;
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

