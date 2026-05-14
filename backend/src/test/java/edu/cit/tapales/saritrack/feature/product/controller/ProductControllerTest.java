package edu.cit.tapales.saritrack.feature.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import edu.cit.tapales.saritrack.feature.product.service.ProductLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ProductLookupService productLookupService;

    // Security-related mocks required by SecurityConfig
    @MockitoBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    
    @MockitoBean
    private edu.cit.tapales.saritrack.feature.auth.repository.UserRepository userRepository;
    
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .vendorId(100L)
                .name("Sardines")
                .barcode("12345")
                .price(25.0)
                .stockQuantity(10)
                .category("Canned Goods")
                .build();
    }

    @Test
    void testLookupByBarcode_ShouldReturnProductName() throws Exception {
        when(productLookupService.lookupProductName(anyString())).thenReturn("Sardines");

        mockMvc.perform(get("/api/products/lookup/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Sardines"));
    }

    @Test
    void testGetByBarcode_Found_ShouldReturnProduct() throws Exception {
        when(productRepository.findByBarcodeAndVendorId("12345", 100L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/barcode/12345?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sardines"));
    }

    @Test
    void testGetByBarcode_NotFound_ShouldReturn404() throws Exception {
        when(productRepository.findByBarcodeAndVendorId(anyString(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/barcode/12345?vendorId=100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll_WithVendorId_ShouldReturnFilteredList() throws Exception {
        when(productRepository.findByVendorId(100L)).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/api/products?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sardines"));
    }

    @Test
    void testSave_ShouldReturnSavedProduct() throws Exception {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sardines"));
    }

    @Test
    void testUpdateProduct_Success_ShouldReturnUpdatedProduct() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(put("/api/products/1?vendorId=100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sardines"));
    }

    @Test
    void testUpdateProduct_Unauthorized_ShouldReturnForbidden() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(put("/api/products/1?vendorId=999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Security Error: You do not own this product."));
    }

    @Test
    void testDelete_Success_ShouldReturnSuccessMessage() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(delete("/api/products/1?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_Unauthorized_ShouldReturnErrorMessage() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(delete("/api/products/1?vendorId=999"))
                .andExpect(status().isOk()) // The controller returns "Error: ..." with status 200 currently
                .andExpect(content().string("Error: Unauthorized or product not found"));

        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteProduct_Unauthorized_OtherVendor_ShouldReturnErrorMessage() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setVendorId(200L); // Different vendor
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(delete("/api/products/1?vendorId=100"))
                .andExpect(status().isOk()) 
                .andExpect(content().string(containsString("Unauthorized")));
    }
}
