package com.waes.test.util;

import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.entity.ProductEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ProductsMapperUtilsTest {

    private ProductDTO productDTO = new ProductDTO()
            .id("1")
            .name("name")
            .price(new BigDecimal("12.01"))
            .quantity(1);

    private UpdateProductDTO updateProductDTO = new UpdateProductDTO()
            .name("name")
            .price(new BigDecimal("12.01"))
            .quantity(1);

    private NewProductDTO newProductDTO = new NewProductDTO()
            .name("name")
            .price(new BigDecimal("12.01"))
            .quantity(1);

    private ProductEntity productEntity = ProductEntity.builder()
            .id("1")
            .name("name")
            .price(new BigDecimal("12.01"))
            .quantity(1)
            .build();

    @Test
    void should_test_productDtofrom_UpdateProductDTO() {
        Assertions.assertEquals(productDTO, ProductsMapperUtils.productDtofrom(updateProductDTO, "1"));
    }

    @Test
    void should_test_productDtofrom_ProductEntity() {
        Assertions.assertEquals(productDTO, ProductsMapperUtils.productDtofrom(productEntity));
    }

    @Test
    void should_test_productEntityfrom_UpdateProductDTO() {
        Assertions.assertEquals(productEntity, ProductsMapperUtils.productEntityfrom(updateProductDTO, "1"));
    }

    @Test
    void should_test_productEntityfrom_NewProductDTO() {
        ProductEntity expected = ProductsMapperUtils.productEntityfrom(newProductDTO);
        Assertions.assertEquals(expected.getName(), newProductDTO.getName());
        Assertions.assertEquals(expected.getPrice(), newProductDTO.getPrice());
        Assertions.assertEquals(expected.getQuantity(), newProductDTO.getQuantity());
        Assertions.assertNotNull(expected.getId());
    }
}