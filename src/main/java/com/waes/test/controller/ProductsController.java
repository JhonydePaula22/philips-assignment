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

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductsController implements V1Api {

    private final ProductService productService;

    @Override
    public ResponseEntity<Void> deleteProduct(@PathVariable(value = "id") String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductDTO> getProduct(@PathVariable(value = "id") String id, @RequestParam(value = "upstream", required = false, defaultValue = "false") String upstream) {
        return ResponseEntity.ok(productService.getProduct(id, Boolean.valueOf(upstream)));
    }

    @Override
    public ResponseEntity<ProductsDTO> getProducts(@RequestParam(value = "upstream", required = false, defaultValue = "false") String upstream) {
        return ResponseEntity.ok(productService.getProducts(Boolean.valueOf(upstream)));
    }

    @Override
    public ResponseEntity<ProductDTO> persistProduct(@RequestBody NewProductDTO body) {
        return ResponseEntity.created(null).body(productService.saveProduct(body));
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody UpdateProductDTO body, @PathVariable(value = "id") String id) {
        return ResponseEntity.ok(productService.updateProduct(body, id));
    }
}
