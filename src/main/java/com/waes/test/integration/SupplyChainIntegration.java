package com.waes.test.integration;

import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;

/**
 * Interface to define the contract of all operations with Supply Chain Service.
 *
 * @author jonathanadepaula
 */
public interface SupplyChainIntegration {

    /**
     * Get a {@link ProductsDTO} which bundles a {@link java.util.List<ProductDTO>}.
     *
     * @return {@link ProductsDTO}
     */
    ProductsDTO getProducts();

    /**
     * Gets a {@link ProductDTO}
     *
     * @param productId
     * @return {@link ProductDTO}
     */
    ProductDTO getProduct(String productId);

    /**
     * Persists a {@link ProductDTO}.
     *
     * @param productDTO {@link ProductDTO}
     * @return {@link ProductDTO}
     */
    ProductDTO createNewProduct(ProductDTO productDTO);

    /**
     * Updates an existing {@link ProductDTO}.
     *
     * @param updateProductDTO {@link UpdateProductDTO}
     * @param productId
     * @return {@link ProductDTO}
     */
    ProductDTO updateProduct(UpdateProductDTO updateProductDTO, String productId);

    /**
     * Deletes a {@link ProductDTO}.
     *
     * @param productId
     */
    void deleteProduct(String productId);
}
