package edu.cit.tapales.saritrack.core.security;

import edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import edu.cit.tapales.saritrack.feature.product.controller.ProductController;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import edu.cit.tapales.saritrack.feature.product.service.ProductLookupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
class SecurityMatrixTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private ProductLookupService productLookupService;
    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private JwtFilter jwtFilter;

    @ParameterizedTest(name = "Endpoint {0} should redirect to login when anonymous")
    @ValueSource(strings = {
        "/api/products",
        "/api/orders",
        "/api/customers",
        "/api/notifications",
        "/api/admin/stats"
    })
    void testAnonymousAccess_ToProtectedEndpoints_ShouldRedirect(String url) throws Exception {
        // In OIDC/OAuth2 configurations, unauthorized access typically redirects (302) to login
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testInvalidToken_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().is3xxRedirection());
    }
}
