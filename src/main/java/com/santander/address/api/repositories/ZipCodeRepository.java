package com.santander.address.api.repositories;

import com.santander.address.api.entities.ZipCode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZipCodeRepository extends PagingAndSortingRepository<ZipCode, UUID>, JpaSpecificationExecutor<ZipCode> {

    Optional<ZipCode> findById(UUID id);

    ZipCode save(ZipCode zipCode);
}
