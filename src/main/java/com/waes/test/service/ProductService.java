package com.waes.test.service;

import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;

public interface ProductService {

    ProductsDTO getProducts(Boolean upstream);

    ProductDTO getProduct(String productId, Boolean upstream);

    ProductDTO saveProduct(NewProductDTO productEntity);

    ProductDTO updateProduct(UpdateProductDTO productEntity, String productId);

    void deleteProduct(String productId);
}
