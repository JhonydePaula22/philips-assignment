package com.waes.test.controller;


import com.waes.test.api.V1Api;
import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Products {@link RestController}.
 *
 * @author jonathanadepaula
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductsController implements V1Api {

    private final ProductService productService;

    /**
     * Deletes a {@link ProductDTO}.
     *
     * @param id
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    public ResponseEntity<Void> deleteProduct(@PathVariable(value = "id") String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets a {@link ProductDTO}
     *
     * @param id
     * @param downstream which indicates if the query should be made on an downstream service
     * @return {@link ResponseEntity<ProductDTO>}
     */
    @Override
    public ResponseEntity<ProductDTO> getProduct(@PathVariable(value = "id") String id, @RequestParam(value = "downstream", required = false, defaultValue = "false") String downstream) {
        return ResponseEntity.ok(productService.getProduct(id, Boolean.valueOf(downstream)));
    }

    /**
     * Get a {@link ProductsDTO} which bundles a {@link java.util.List<ProductDTO>}.
     *
     * @param downstream which indicates if the query should be made on an downstream service
     * @return {@link ResponseEntity<ProductsDTO>}
     */
    @Override
    public ResponseEntity<ProductsDTO> getProducts(@RequestParam(value = "downstream", required = false, defaultValue = "false") String downstream) {
        return ResponseEntity.ok(productService.getProducts(Boolean.valueOf(downstream)));
    }

    /**
     * Persists a {@link ProductDTO}.
     *
     * @param body {@link NewProductDTO}
     * @return {@link ResponseEntity<ProductDTO>}
     */
    @Override
    public ResponseEntity<ProductDTO> persistProduct(@RequestBody NewProductDTO body) {
        return ResponseEntity.created(null).body(productService.saveProduct(body));
    }

    /**
     * Updates an existing {@link ProductDTO}.
     *
     * @param body {@link UpdateProductDTO}
     * @param id
     * @return {@link ResponseEntity<ProductDTO>}
     */
    @Override
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody UpdateProductDTO body, @PathVariable(value = "id") String id) {
        return ResponseEntity.ok(productService.updateProduct(body, id));
    }
}
