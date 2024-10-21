package com.santander.address.api.services;

import com.santander.address.api.configurations.AllowedFilters;
import com.santander.address.api.entities.Product;
import com.santander.address.api.exceptions.BadRequestException;
import com.santander.address.api.exceptions.ConflictException;
import com.santander.address.api.exceptions.NotFoundException;
import com.santander.address.api.repositories.ProductRepository;
import com.santander.address.api.requests.ProductRequest;
import com.santander.address.api.specifications.GenericSpecification;
import com.santander.address.api.utils.FilterItem;
import com.santander.address.api.utils.Message;
import com.santander.address.api.utils.Paginate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public void delete(UUID id) {
        Product product = findById(id);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public Page<Product> findAll(Integer page, Integer size, List<FilterItem> filters) {
        Pageable pageable = Paginate.from(page, size);
        GenericSpecification<Product> specification = GenericSpecification.from(filters, AllowedFilters.product());
        return productRepository.findAll(specification, pageable);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(Message.PRODUCT_NOT_FOUND));
    }

    public Product save(ProductRequest productRequest) {
        if (productRequest.getName() == null || productRequest.getDescription() == null) {
            throw new BadRequestException(Message.INVALID_REQUEST);
        }

        if (productRepository.existsByName(productRequest.getName())) {
            throw new ConflictException(Message.PRODUCT_CONFLICT);
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .deletedAt(productRequest.getDeletedAt())
                .build();

        return productRepository.save(product);
    }

    public void partialUpdate(UUID id, ProductRequest productRequest) {
        Product product = findById(id);

        if (productRequest.getName() != null) {
            Optional<Product> productByName = productRepository.findByName(productRequest.getName());

            if (productByName.isPresent() && !productByName.get().getId().equals(id)) {
                throw new ConflictException(Message.PRODUCT_CONFLICT);
            }

            product.setName(productRequest.getName());
        }
        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }
        if (productRequest.getDeletedAt() == null && product.getDeletedAt() != null) {
            product.setDeletedAt(null);
        }

        productRepository.save(product);
    }
}
