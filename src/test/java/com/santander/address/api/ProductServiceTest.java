package com.santander.address.api;

import com.santander.address.api.entities.Product;
import com.santander.address.api.exceptions.ConflictException;
import com.santander.address.api.exceptions.NotFoundException;
import com.santander.address.api.repositories.ProductRepository;
import com.santander.address.api.requests.ProductRequest;
import com.santander.address.api.services.ProductService;
import com.santander.address.api.utils.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private UUID id;
    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    public void setUp() {
        id = UUID.randomUUID();
        product = new Product();
        product.setId(id);
        product.setDeletedAt(LocalDateTime.now());
        productRequest = new ProductRequest();
        productRequest.setDescription("description");
        productRequest.setName("name");
    }

    @Test
    public void testDelete() {
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        productService.delete(id);
        assertNotNull(product.getDeletedAt());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void testDeleteNotFound() {
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.delete(id));
        assertEquals(Message.PRODUCT_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void testFindAll() {
        PageImpl<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        Page<Product> result = productService.findAll(1, 10, List.of());
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindById() {
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Product entity = productService.findById(id);
        assertEquals(product, entity);
    }

    @Test
    public void testFindByIdNotFound() {
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.findById(id));
        assertEquals(Message.PRODUCT_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void testSave() {
        when(productRepository.existsByName(productRequest.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product entity = productService.save(productRequest);
        assertNotNull(entity);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testSaveConflict() {
        when(productRepository.existsByName(productRequest.getName())).thenReturn(true);
        ConflictException exception = assertThrows(ConflictException.class, () -> productService.save(productRequest));
        assertEquals(Message.PRODUCT_CONFLICT, exception.getMessage());
    }

}
