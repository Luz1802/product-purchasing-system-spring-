package co.edu.cesde.pps;

import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.OrderRepository;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Etapa14UserSelfServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        addressRepository.deleteAll();
        cartRepository.deleteAll();
        userSessionRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        roleRepository.save(Role.builder()
                .name("CUSTOMER")
                .description("Regular customer user")
                .build());
    }

    @Test
    void shouldAllowAuthenticatedUserToUpdateOwnProfile() throws Exception {
        String sessionToken = registerUser("ada@cesde.edu.co", "secret123").path("sessionToken").asText();

        mockMvc.perform(put("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Augusta Ada",
                                  "lastName": "King",
                                  "phone": "3017654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@cesde.edu.co"))
                .andExpect(jsonPath("$.firstName").value("Augusta Ada"))
                .andExpect(jsonPath("$.lastName").value("King"))
                .andExpect(jsonPath("$.fullName").value("Augusta Ada King"))
                .andExpect(jsonPath("$.phone").value("3017654321"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Augusta Ada"))
                .andExpect(jsonPath("$.lastName").value("King"))
                .andExpect(jsonPath("$.fullName").value("Augusta Ada King"));
    }

    @Test
    void shouldAllowAuthenticatedUserToChangeOwnPasswordWithoutInvalidatingCurrentSession() throws Exception {
        String sessionToken = registerUser("grace@cesde.edu.co", "secret123").path("sessionToken").asText();

        mockMvc.perform(put("/api/v1/users/me/password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "secret123",
                                  "newPassword": "secret456"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("grace@cesde.edu.co"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "grace@cesde.edu.co",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "grace@cesde.edu.co",
                                  "password": "secret456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("grace@cesde.edu.co"));
    }

    @Test
    void shouldReturnExpectedErrorsForProfileAndPasswordSelfServiceEndpoints() throws Exception {
        String sessionToken = registerUser("linus@cesde.edu.co", "secret123").path("sessionToken").asText();

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Linus",
                                  "lastName": "Torvalds",
                                  "phone": "3001234567"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/v1/users/me"));

        MvcResult invalidProfileResult = mockMvc.perform(put("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "",
                                  "phone": "3001234567"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andReturn();

        Set<String> invalidProfileFields = extractInvalidFields(invalidProfileResult);
        assertThat(invalidProfileFields).contains("firstName", "lastName");

        mockMvc.perform(put("/api/v1/users/me/password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "wrong-secret",
                                  "newPassword": "secret456"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/v1/users/me/password"));

        MvcResult invalidPasswordResult = mockMvc.perform(put("/api/v1/users/me/password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(sessionToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "secret123",
                                  "newPassword": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andReturn();

        assertThat(extractInvalidFields(invalidPasswordResult)).contains("newPassword");
    }

    private JsonNode registerUser(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s",
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "phone": "3001234567"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private Set<String> extractInvalidFields(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        Set<String> invalidFields = new HashSet<>();
        body.path("details").forEach(detail -> invalidFields.add(detail.path("field").asText()));
        return invalidFields;
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

