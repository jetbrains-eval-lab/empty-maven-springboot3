package eval.sample.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthControllerTest.UserController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Get admin token using Basic Authentication
        String adminCredentials = "admin:admin123";
        String encodedAdminCredentials = Base64.getEncoder().encodeToString(adminCredentials.getBytes());
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Basic " + encodedAdminCredentials))
                .andExpect(status().isOk())
                .andReturn();
        AuthResponse adminResponse = objectMapper.readValue(adminResult.getResponse().getContentAsString(), AuthResponse.class);
        adminToken = adminResponse.token();

        // Get user token using Basic Authentication
        String userCredentials = "user:user123";
        String encodedUserCredentials = Base64.getEncoder().encodeToString(userCredentials.getBytes());
        MvcResult userResult = mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Basic " + encodedUserCredentials))
                .andExpect(status().isOk())
                .andReturn();
        AuthResponse userResponse = objectMapper.readValue(userResult.getResponse().getContentAsString(), AuthResponse.class);
        userToken = userResponse.token();
    }

    @Test
    void loginSuccess() throws Exception {
        // Create Basic Authentication header
        String credentials = "admin:admin123";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Perform login request
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Basic " + encodedCredentials))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        String responseJson = result.getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(responseJson, AuthResponse.class);

        // Verify response
        assertNotNull(response.token());
        assertEquals("admin", response.username());
    }

    @Test
    void loginFailure() throws Exception {
        // Create Basic Authentication header with invalid credentials
        String credentials = "admin:wrongpassword";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Perform login request and expect forbidden status
        mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Basic " + encodedCredentials))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUserAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.authorities").isArray());
    }

    @Test
    void getCurrentUserAsUser() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.authorities").isArray());
    }

    @Test
    void accessAdminEndpointAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/admin")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminEndpointAsUser() throws Exception {
        mockMvc.perform(get("/api/users/admin")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessUserEndpointAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/user")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void accessUserEndpointAsUser() throws Exception {
        mockMvc.perform(get("/api/users/user")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @RestController
    @RequestMapping("/api/users")
    public static class UserController {

        // Constructor
        public UserController() {
        }

        @GetMapping("/me")
        public ResponseEntity<Map<String, Object>> getCurrentUser() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", authentication.getName());
            userInfo.put("authorities", authentication.getAuthorities());

            return ResponseEntity.ok(userInfo);
        }

        @GetMapping("/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<String> adminOnly() {
            return ResponseEntity.ok("Hello Admin! This endpoint is restricted to users with ADMIN role.");
        }

        @GetMapping("/user")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<String> userOnly() {
            return ResponseEntity.ok("Hello User! This endpoint is restricted to users with USER role.");
        }
    }
}

record AuthResponse(
    String token,
    String username,
    String tokenType
){}