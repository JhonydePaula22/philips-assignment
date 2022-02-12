package com.waes.test.service;

import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import org.springframework.http.ResponseEntity;

/**
 * Interface to define the contract of all operations with Product.
 *
 * @author jonathanadepaula
 */
public interface ProductService {

    /**
     * Get a {@link ProductsDTO} which bundles a {@link java.util.List<ProductDTO>}.
     *
     * @param upstream which indicates if the query should be made on an upstream service
     * @return {@link ResponseEntity <ProductsDTO>}
     */
    ProductsDTO getProducts(Boolean upstream);

    /**
     * Gets a {@link ProductDTO}
     *
     * @param productId
     * @param upstream  which indicates if the query should be made on an upstream service
     * @return {@link ResponseEntity<ProductDTO>}
     */
    ProductDTO getProduct(String productId, Boolean upstream);

    /**
     * Persists a {@link ProductDTO}.
     *
     * @param productEntity {@link NewProductDTO}
     * @return {@link ResponseEntity<ProductDTO>}
     */
    ProductDTO saveProduct(NewProductDTO productEntity);

    /**
     * Updates an existing {@link ProductDTO}.
     *
     * @param productEntity {@link UpdateProductDTO}
     * @param productId
     * @return {@link ResponseEntity<ProductDTO>}
     */
    ProductDTO updateProduct(UpdateProductDTO productEntity, String productId);

    /**
     * Deletes a {@link ProductDTO}.
     *
     * @param productId
     * @return {@link ResponseEntity<Void>}
     */
    void deleteProduct(String productId);
}
