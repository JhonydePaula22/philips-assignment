package com.waes.test.util;

import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.entity.ProductEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProductsMapperUtils {

    public static ProductDTO productDtofrom(UpdateProductDTO newProductDTO, String productId) {
        return new ProductDTO()
                .id(productId)
                .name(newProductDTO.getName())
                .price(newProductDTO.getPrice())
                .quantity(newProductDTO.getQuantity());
    }

    public static ProductDTO productDtofrom(ProductEntity productEntity) {
        return new ProductDTO()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .quantity(productEntity.getQuantity());
    }

    public static ProductEntity productEntityfrom(NewProductDTO newProductDTO) {
        return ProductEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(newProductDTO.getName())
                .price(newProductDTO.getPrice())
                .quantity(newProductDTO.getQuantity())
                .build();
    }

    public static ProductEntity productEntityfrom(UpdateProductDTO newProductDTO, String productId) {
        return ProductEntity.builder()
                .id(productId)
                .name(newProductDTO.getName())
                .price(newProductDTO.getPrice())
                .quantity(newProductDTO.getQuantity())
                .build();
    }
}
