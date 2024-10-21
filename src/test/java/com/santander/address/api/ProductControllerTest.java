package com.santander.address.api;

import com.santander.address.api.controllers.ProductController;
import com.santander.address.api.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDelete() {
        UUID id = UUID.randomUUID();
        doNothing().when(productService).delete(id);
        ResponseEntity<?> response = productController.delete(id);
        assertEquals(ResponseEntity.noContent().build(), response);
        verify(productService, times(1)).delete(id);
    }

}
