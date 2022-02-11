package com.waes.test.integration.impl;

import com.waes.test.exception.BadRequestException;
import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.util.HttpUtils;
import com.waes.test.util.ProductsMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public class SupplyChainIntegrationImpl implements SupplyChainIntegration {

    private final String supplyChainIntegrationBasePath;
    private final String supplyChainIntegrationResourcesPath;
    private final HttpUtils httpUtils;
    private final String url;

    public SupplyChainIntegrationImpl(@Value("${supply.chain.base.url}") String supplyChainIntegrationBasePath,
                                      @Value("${supply.chain.resources.path}") String supplyChainIntegrationResourcesPath,
                                      HttpUtils httpUtils) {
        this.supplyChainIntegrationBasePath = supplyChainIntegrationBasePath;
        this.supplyChainIntegrationResourcesPath = supplyChainIntegrationResourcesPath;
        this.httpUtils = httpUtils;
        this.url = supplyChainIntegrationBasePath.concat(supplyChainIntegrationResourcesPath);
    }

    @Override
    public ProductsDTO getProducts() {
        log.info("Getting All Products from Supply Chain Integration.");
        Supplier<ProductsDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductsDTO.class);
        Optional<ProductsDTO> optional = Optional.ofNullable(httpUtils.executeCall(getProductsDTOSupplier));
        if (optional.isPresent()) {
            return optional.get();
        }
        log.warn("The  from Supply Chain Integration API has not returned a list of products.");
        return new ProductsDTO();
    }

    @Override
    public ProductDTO getProduct(String productId) {
        log.info("Getting Product with id {} from Supply Chain Integration.", productId);
        Supplier<ProductDTO> getProductDTOSupplier = () -> httpUtils.executeGetRequest(getUrlWithProductId(productId), ProductDTO.class);
        return httpUtils.executeCall(getProductDTOSupplier);
    }

    @Override
    public ProductDTO createNewProduct(ProductDTO productDTO) {
        log.info("Creating Product with data {} on Supply Chain Integration.", productDTO);
        Supplier<ProductDTO> createNewProductSupplier = () -> httpUtils.executePostRequest(this.url, productDTO, ProductDTO.class);
        return httpUtils.executeCallWithObserver(createNewProductSupplier, productDTO, EventEnum.CREATE);
    }

    @Override
    public ProductDTO updateProduct(UpdateProductDTO updateProductDTO, String productId) {
        log.info("Updating Product with data {} and id {} on Supply Chain Integration.", updateProductDTO, productId);
        validateProductId(productId);
        ProductDTO productDTO = ProductsMapperUtils.productDtofrom(updateProductDTO, productId);
        Supplier<ProductDTO> updateProductSupplier = () -> httpUtils.executePostRequest(this.url, productDTO, ProductDTO.class);
        return httpUtils.executeCallWithObserver(updateProductSupplier, productDTO, EventEnum.UPDATE);
    }

    @Override
    public void deleteProduct(String productId) {
        log.info("Deleting product with id {} from Supply Chain Integration.", productId);
        validateProductId(productId);
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(getUrlWithProductId(productId), Void.class);
        httpUtils.executeCallWithObserver(deleteProductSupplier, new ProductDTO().id(productId), EventEnum.DELETE);
    }

    private void validateProductId(String productId) {
        try {
            getProduct(productId);
        } catch (BadRequestException e) {
            throw new BadRequestException(String.format("We could not find a valid Product with the provided Id %s.", productId));
        }
    }

    private String getUrlWithProductId(String productId) {
        return this.url.concat("/").concat(productId);
    }
}
