package com.waes.test.service.impl;

import com.waes.test.exception.BadRequestException;
import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.entity.ProductEntity;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.Observer;
import com.waes.test.repository.ProductRepository;
import com.waes.test.service.ProductService;
import com.waes.test.util.ProductsMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * {@link Service} class to handle Operations with Product.
 * Implements {@link ProductService} interface.
 *
 * @author jonathanadepaula
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final SupplyChainIntegration supplyChainIntegration;
    private final Observer<ProductDTO> observer;

    public ProductServiceImpl(ProductRepository repository,
                              SupplyChainIntegration supplyChainIntegration,
                              @Qualifier("propagationObserver")
                                      Observer<ProductDTO> observer) {
        this.repository = repository;
        this.supplyChainIntegration = supplyChainIntegration;
        this.observer = observer;
    }

    @Override
    public ProductsDTO getProducts(Boolean upstream) {
        if (upstream) {
            return supplyChainIntegration.getProducts();
        }

        log.info("Getting All Products.");
        List<ProductDTO> productDTOList = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(ProductsMapperUtils::productDtofrom)
                .collect(Collectors.toList());

        return new ProductsDTO().bundle(productDTOList);
    }

    @Override
    public ProductDTO getProduct(String productId, Boolean upstream) {
        if (upstream) {
            return supplyChainIntegration.getProduct(productId);
        }

        log.info("Getting Product with id {}.", productId);
        ProductEntity productEntity = repository.findById(productId).orElseThrow(() -> {
            log.error("Failed to retrieve product from internal database if id {}.", productId);
            throw new BadRequestException("Product not found with the provided Id");
        });
        return ProductsMapperUtils.productDtofrom(productEntity);
    }

    @Override
    public ProductDTO saveProduct(NewProductDTO newProductDTO) {
        log.info("Creating Product with data {}.", newProductDTO);
        ProductEntity productEntity = repository.save(ProductsMapperUtils.productEntityfrom(newProductDTO));
        ProductDTO productDTO = ProductsMapperUtils.productDtofrom(productEntity);
        observer.notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.PROPAGATE);
        return productDTO;
    }

    @Override
    @CacheEvict(value = "product", key = "#productId")
    public ProductDTO updateProduct(UpdateProductDTO updateProductDTO, String productId) {
        log.info("Updating Product with data {} and id {}.", updateProductDTO, productId);
        validateId(productId);
        ProductEntity productEntity = repository.save(ProductsMapperUtils.productEntityfrom(updateProductDTO, productId));
        ProductDTO productDTO = ProductsMapperUtils.productDtofrom(productEntity);
        observer.notifyObserver(productDTO, ActionEnum.UPDATE, EventTypeEnum.PROPAGATE);
        return productDTO;
    }

    @Override
    @CacheEvict(value = "product", key = "#productId")
    public void deleteProduct(String productId) {
        log.info("Deleting product with id {}.", productId);
        validateId(productId);
        repository.deleteById(productId);
        observer.notifyObserver(new ProductDTO().id(productId), ActionEnum.DELETE, EventTypeEnum.PROPAGATE);
    }

    private void validateId(String productId) {
        if (repository.countById(productId) == 0) {
            throw new BadRequestException(String.format("We could not find a valid Product with the provided Id %s.", productId));
        }
    }
}
