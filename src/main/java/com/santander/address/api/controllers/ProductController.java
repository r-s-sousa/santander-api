package com.santander.address.api.controllers;

import com.santander.address.api.entities.Product;
import com.santander.address.api.requests.ProductRequest;
import com.santander.address.api.responses.ProductResponse;
import com.santander.address.api.services.ProductService;
import com.santander.address.api.utils.DynamicFilter;
import com.santander.address.api.utils.FilterItem;
import com.santander.address.api.utils.Paginate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/v1")
@RestController
@Tag(name = "products", description = "products management")
public class ProductController {

    private final ProductService productService;

    @PostMapping(path = "/products")
    public ResponseEntity<ProductResponse> save(@RequestBody @Valid ProductRequest productRequest,
                                                UriComponentsBuilder uriComponentsBuilder) {
        Product product = productService.save(productRequest);
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .deletedAt(product.getDeletedAt())
                .createdAt(product.getCreatedAt())
                .build();
        URI uri = uriComponentsBuilder.path("/v1/products/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(productResponse);
    }

    @GetMapping(path = "/products")
    public ResponseEntity<Paginate<Product, ProductResponse>> findAll(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "") Map<String, String> params) {
        List<FilterItem> filters = DynamicFilter.parseFilters(params);
        Page<Product> products = productService.findAll(page, size, filters);
        List<ProductResponse> productsResponse = new ArrayList<>();

        products.forEach(product -> {
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .deletedAt(product.getDeletedAt())
                    .createdAt(product.getCreatedAt())
                    .build();
            productsResponse.add(productResponse);
        });

        return ResponseEntity.ok(Paginate.from(products, productsResponse));
    }

    @GetMapping(path = "/products/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        Product product = productService.findById(id);
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .deletedAt(product.getDeletedAt())
                .createdAt(product.getCreatedAt())
                .build();
        return ResponseEntity.ok(productResponse);
    }

    @PatchMapping(path = "/products/{id}")
    public ResponseEntity<Void> partialUpdate(@PathVariable UUID id,
                                              @RequestBody @Valid ProductRequest productRequest) {
        productService.partialUpdate(id, productRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
