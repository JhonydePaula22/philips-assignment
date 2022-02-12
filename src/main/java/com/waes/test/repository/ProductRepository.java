package com.waes.test.repository;

import com.waes.test.model.entity.ProductEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Interface to handle the Crud Opeations on {@link ProductEntity}.
 * extends {@link CrudRepository}
 *
 * @author jonathanadepaula
 */
public interface ProductRepository extends CrudRepository<ProductEntity, String> {

    /**
     * Counts how many Products are in the table with the given id.
     *
     * @param id
     * @return int
     */
    int countById(String id);

    /**
     * Cached query to get a product by a given id.
     *
     * @param id
     * @return {@link Optional<ProductEntity>}
     */
    @Override
    @Cacheable(value = "product")
    Optional<ProductEntity> findById(String id);
}
