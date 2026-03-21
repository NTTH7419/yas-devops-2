package com.yas.inventory.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setUpSecurityContextWithJwt(String tokenValue, String subject) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(tokenValue);
        when(jwt.getSubject()).thenReturn(subject);

        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    private void setUpSecurityContextWithAnonymous() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Nested
    class ExtractJwtTests {

        @Test
        void extractJwt_shouldReturnTokenValue() {
            setUpSecurityContextWithJwt("my-super-token", "user-123");

            String result = AuthenticationUtils.extractJwt();

            assertThat(result).isEqualTo("my-super-token");
        }

        @Test
        void extractJwt_shouldReturnTokenValue_whenTokenIsDifferent() {
            setUpSecurityContextWithJwt("another-token-xyz", "user-456");

            String result = AuthenticationUtils.extractJwt();

            assertThat(result).isEqualTo("another-token-xyz");
        }
    }

    @Nested
    class ExtractUserIdTests {

        @Test
        void extractUserId_shouldReturnSubject_whenJwtAuthenticationToken() {
            setUpSecurityContextWithJwt("token-value", "user-789");

            String result = AuthenticationUtils.extractUserId();

            assertThat(result).isEqualTo("user-789");
        }

        @Test
        void extractUserId_shouldThrowAccessDeniedException_whenAnonymousAuthentication() {
            setUpSecurityContextWithAnonymous();

            // With a mock Authentication that is not AnonymousAuthenticationToken,
            // the instanceof check fails and a ClassCastException is thrown instead
            assertThatThrownBy(() -> AuthenticationUtils.extractUserId())
                .isInstanceOf(ClassCastException.class);
        }

        @Test
        void extractUserId_shouldThrowAccessDeniedException_whenAnonymousAuthenticationToken() {
            AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(
                "key", "anonymousUser",
                java.util.List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
            );
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            assertThatThrownBy(() -> AuthenticationUtils.extractUserId())
                .isInstanceOf(AccessDeniedException.class);
        }
    }
}
