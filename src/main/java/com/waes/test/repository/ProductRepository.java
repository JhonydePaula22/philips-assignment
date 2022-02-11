package com.waes.test.repository;

import com.waes.test.model.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, String> {

    int countById(String id);
}
