package com.santander.address.api.repositories;

import com.santander.address.api.entities.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Boolean existsByName(String name);

    Optional<Product> findById(UUID id);

    Product save(Product product);

    Optional<Product> findByName(String name);
}
