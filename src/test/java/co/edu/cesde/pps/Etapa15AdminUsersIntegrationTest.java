package co.edu.cesde.pps;

import co.edu.cesde.pps.enums.UserStatus;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.security.PasswordHasher;
import co.edu.cesde.pps.service.UserSessionService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Etapa15AdminUsersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private UserSessionService userSessionService;

    private Role adminRole;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        addressRepository.deleteAll();
        userSessionRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        adminRole = roleRepository.save(Role.builder()
                .name("ADMIN")
                .description("Administrator user")
                .build());

        customerRole = roleRepository.save(Role.builder()
                .name("CUSTOMER")
                .description("Regular customer user")
                .build());
    }

    @Test
    void shouldReturnUnauthorizedWhenAdminUsersEndpointHasNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnForbiddenWhenCustomerCallsAdminUsersEndpoint() throws Exception {
        String customerToken = createAuthenticatedSession(customerRole, "customer@cesde.edu.co");

        mockMvc.perform(get("/api/v1/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldAllowAdminToCreateListReadUpdateAndSoftDeleteUsers() throws Exception {
        String adminToken = createAuthenticatedSession(adminRole, "admin@cesde.edu.co");

        String createResponse = mockMvc.perform(post("/api/v1/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload("new.user@cesde.edu.co", "secret123", "CUSTOMER", "ACTIVE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new.user@cesde.edu.co"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdUserId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(createResponse)
                .path("id")
                .asLong();

        mockMvc.perform(get("/api/v1/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("new.user@cesde.edu.co")));

        mockMvc.perform(get("/api/v1/admin/users/{id}", createdUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserId))
                .andExpect(jsonPath("$.email").value("new.user@cesde.edu.co"));

        mockMvc.perform(put("/api/v1/admin/users/{id}", createdUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload("updated.user@cesde.edu.co", "ADMIN", "ACTIVE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated.user@cesde.edu.co"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.phone").value("3017654321"));

        mockMvc.perform(delete("/api/v1/admin/users/{id}", createdUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admin/users/{id}", createdUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        User deletedUser = userRepository.findById(createdUserId).orElseThrow();
        assertThat(deletedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "updated.user@cesde.edu.co",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnConflictWhenAdminCreatesDuplicateEmail() throws Exception {
        String adminToken = createAuthenticatedSession(adminRole, "admin@cesde.edu.co");

        mockMvc.perform(post("/api/v1/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload("admin@cesde.edu.co", "secret123", "ADMIN", "ACTIVE")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
    }

    @Test
    void shouldRejectInvalidStatusOnCreate() throws Exception {
        String adminToken = createAuthenticatedSession(adminRole, "admin@cesde.edu.co");

        mockMvc.perform(post("/api/v1/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload("blocked.user@cesde.edu.co", "secret123", "CUSTOMER", "BLOCKED")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
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

    private String createPayload(String email, String password, String role, String status) {
        return """
                {
                  "email": "%s",
                  "password": "%s",
                  "firstName": "Nuevo",
                  "lastName": "Usuario",
                  "phone": "3001234567",
                  "role": "%s",
                  "status": "%s"
                }
                """.formatted(email, password, role, status);
    }

    private String updatePayload(String email, String role, String status) {
        return """
                {
                  "email": "%s",
                  "firstName": "Usuario",
                  "lastName": "Actualizado",
                  "phone": "3017654321",
                  "role": "%s",
                  "status": "%s"
                }
                """.formatted(email, role, status);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

