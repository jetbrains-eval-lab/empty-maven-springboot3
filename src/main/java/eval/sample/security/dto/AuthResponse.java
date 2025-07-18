package eval.sample.security.dto;

import java.util.Objects;

public class AuthResponse {
    private String token;
    private String username;
    private String tokenType = "Bearer";

    // No-args constructor
    public AuthResponse() {
    }

    // All-args constructor
    public AuthResponse(String token, String username, String tokenType) {
        this.token = token;
        this.username = username;
        this.tokenType = tokenType;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(username, that.username) &&
                Objects.equals(tokenType, that.tokenType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, username, tokenType);
    }

    // toString
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }

    // Builder pattern
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String username;
        private String tokenType = "Bearer";

        AuthResponseBuilder() {
        }

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, username, tokenType);
        }
    }
}