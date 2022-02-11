package com.waes.test.integration;

import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;

public interface SupplyChainIntegration {

    ProductsDTO getProducts();

    ProductDTO getProduct(String productId);

    ProductDTO createNewProduct(ProductDTO productDTO);

    ProductDTO updateProduct(UpdateProductDTO updateProductDTO, String productId);

    void deleteProduct(String productId);
}
