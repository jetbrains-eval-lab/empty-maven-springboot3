package eval.sample.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import eval.sample.security.dto.AuthResponse;
import eval.sample.security.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    
    // Constructor with required arguments
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(HttpServletRequest request) {
        // Extract credentials from Basic Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String password = values[1];
            
            AuthResponse response = authenticationService.authenticate(username, password);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).build();
    }
}